package kr.shkworld.shktown.ui.apps.navigation;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.core.common.model.Position;
import kr.shkworld.shktown.core.navigation.model.NavigationSortType;
import kr.shkworld.shktown.core.navigation.service.NavigationService;
import kr.shkworld.shktown.ui.SmartphoneScreen;
import kr.shkworld.shktown.util.GUIUtil;
import kr.shkworld.shktown.util.LocationUtil;
import kr.shkworld.shktown.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class NavigationScreen implements SmartphoneScreen {
    private final SHKTown plugin;
    private final Inventory inventory;
    private final Player player;

    private int currentPage = 0;
    private NavigationSortType sortType = NavigationSortType.NAME_ASC;
    private String searchQuery = "";

    private static final int[] CONTENT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public NavigationScreen(SHKTown plugin, Player player) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 54, MessageUtil.parse("§8GPS 메뉴"));
        this.player = player;

        refresh();
    }

    private void refresh() {
        NavigationService navigationService = plugin.getNavigationService();
        NavigationManager navigationManager = plugin.getNavigationManager();

        ItemStack bezel = GUIUtil.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "&f", null);
        for (int i = 0; i < 54; i++) {
            if (i <= 3 || (i >= 5 && i <= 8) || i == 9 || i == 17 || i == 18 || i == 26 ||
                    i == 27 || i == 35 || i ==  36 || i >= 45) {
                inventory.setItem(i, bezel);
            }
        }

        boolean isNavigating = navigationManager.isNavigating(player);
        String destinationName = navigationManager.getDestinationName(player);

        Map<String, Position> destinationMap = navigationService.getDestinations();
        Location playerLocation = player.getLocation();
        Position playerPosition = LocationUtil.toPosition(playerLocation);

        List<Map.Entry<String, Position>> filteredList = destinationMap.entrySet().stream()
                .filter(entry -> searchQuery.isEmpty() || entry.getKey().contains(searchQuery))
                .sorted(sortType.getComparator(playerPosition))
                .toList();

        int maxItemPerPage = CONTENT_SLOTS.length;
        int startIndex = currentPage * maxItemPerPage;
        int endIndex = Math.min(startIndex + maxItemPerPage, filteredList.size());
        int totalPages = Math.max(1, (int) Math.ceil((double) filteredList.size() / maxItemPerPage));

        ItemStack status = GUIUtil.createItem(Material.PAPER, "&b&lGPS 상태", null,
                "&f내비게이션 상태: " + (isNavigating ? ("&a" + destinationName + "(으)로 안내 중") : "&c대기 중"),
                "&f페이지: &b" + (currentPage + 1) + "/" + totalPages,
                "&f현재 정렬 기준: &e" + sortType.getDisplayName(),
                "&f검색 필터: &b" + (searchQuery.isEmpty() ? "없음" : searchQuery)
        );
        inventory.setItem(4, status);

        for (int i = startIndex; i < endIndex; i++) {
            Map.Entry<String, Position> entry = filteredList.get(i);
            String destName = entry.getKey();
            Position destPos = entry.getValue();

            int slot = CONTENT_SLOTS[i - startIndex];

            boolean isSameWorld = playerLocation.getWorld() != null
                    && playerLocation.getWorld().getName().equalsIgnoreCase(destPos.world());

            String distanceText;
            if (isSameWorld) {
                double distance = playerPosition.distance(destPos);
                distanceText = String.format("&f거리: &b%.1fm", distance);
            } else {
                distanceText = "&f거리: &c다른 월드 (&e" + destPos.world() + "&c)";
            }

            ItemStack compass = GUIUtil.createItem(Material.COMPASS,
                    "&b" + destName,
                    null,
                    String.format("&f좌표: &7X %.0f, Y: %.0f, Z: %.0f", destPos.x(), destPos.y(), destPos.z()),
                    distanceText,
                    "&f",
                    "&e클릭하여 길 안내 시작"
            );

            inventory.setItem(slot, compass);
        }

        if (currentPage > 0) {
            inventory.setItem(45, GUIUtil.createItem(Material.ARROW,
                    "&a이전 페이지 &f(" + currentPage + "/" + totalPages + ")", null));
        } else {
            inventory.setItem(45, bezel);
        }

        inventory.setItem(48, GUIUtil.createItem(Material.HOPPER,
                "&d정렬 방식 설정",
                null,
                "&f현재: &e" + sortType.getDisplayName(),
                "&f",
                "&e클릭 시 다음 정렬 방식으로 변경")
        );

        inventory.setItem(49, GUIUtil.createItem(Material.COMPASS, "§cHOME", "§7스마트폰 메인 화면으로 복귀합니다."));

        inventory.setItem(50, GUIUtil.createItem(Material.SPYGLASS,
                "&b목적지 검색",
                null,
                "&e좌클릭하여 검색어 입력",
                "&c우클릭하여 검색어 초기화")
        );

        inventory.setItem(52, GUIUtil.createItem(Material.BARRIER,
                "&c길 안내 중지",
                null,
                "&f현재 진행 중인 길 안내를 종료합니다.")
        );

        if (currentPage < totalPages - 1) {
            inventory.setItem(53, GUIUtil.createItem(Material.ARROW,
                    "&a다음 페이지 &f(" + (currentPage + 2) + "/" + totalPages + ")", null));
        } else {
            inventory.setItem(53, bezel);
        }
    }

    @Override
    public void handleSlotClick(Player player, int slot) {
        handleSlotClick(player, slot, ClickType.LEFT);
    }

    @Override
    public void handleSlotClick(Player player, int slot, ClickType clickType) {
        NavigationService navigationService = plugin.getNavigationService();
        NavigationManager navigationManager = plugin.getNavigationManager();

        if (slot == 45 && currentPage > 0) {
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType() == Material.ARROW) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                currentPage--;
                refresh();
                return;
            }
        }

        if (slot == 48) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            sortType = sortType.next();
            refresh();
            return;
        }

        if (slot == 49) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            plugin.getSmartphoneManager().openMainScreen(player);
            return;
        }

        if (slot == 50) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            MessageUtil.send(player, "&c아직 구현 중인 기능입니다.");
            return;
        }

        if (slot == 52) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            if (navigationManager.isNavigating(player)) {
                navigationManager.stopNavigation(player, false);
                refresh();
            } else {
                MessageUtil.send(player, "&c현재 진행 중인 길 안내가 없습니다.");
            }
            return;
        }

        if (slot == 53) {
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType() == Material.ARROW) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                currentPage++;
                refresh();
                return;
            }
        }

        for (int contentSlot : CONTENT_SLOTS) {
            if (slot == contentSlot) {
                ItemStack item = inventory.getItem(slot);
                if (item != null && item.getType() == Material.COMPASS && item.hasItemMeta()) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                    String rawName = MessageUtil.serializePlainText(item.getItemMeta().displayName());
                    String destName = MessageUtil.stripColor(rawName).trim();

                    Position destPos = navigationService.getDestination(destName);
                    if (destPos != null) {
                        Location destLoc = LocationUtil.toLocation(destPos);

                        if (destLoc.getWorld() == null || !player.getWorld().equals(destLoc.getWorld())) {
                            MessageUtil.send(player, navigationService.getConfig().differentWorld(), false);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1f, 2f);
                            return;
                        }

                        navigationManager.startNavigation(player, destName, destLoc);
                        player.closeInventory();
                    }
                }
                break;
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

}