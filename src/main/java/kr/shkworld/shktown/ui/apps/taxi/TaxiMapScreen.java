package kr.shkworld.shktown.ui.apps.taxi;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.core.common.model.Position;
import kr.shkworld.shktown.core.taxi.model.TaxiMap;
import kr.shkworld.shktown.core.taxi.model.TaxiStop;
import kr.shkworld.shktown.core.taxi.service.TaxiService;
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
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

public class TaxiMapScreen implements SmartphoneScreen {
    private final SHKTown plugin;
    private final TaxiMap taxiMap;
    private final Inventory inventory;

    private static final Map<String, NaviInfo> NAVI_MAP = Map.of(
            "up", new NaviInfo(4, "§a▲ 위쪽 지도로 이동"),
            "left",  new NaviInfo(18, "§a◀ 왼쪽 지도로 이동"),
            "right",  new NaviInfo(26, "§a오른쪽 지도로 이동 ▶"),
            "down", new NaviInfo(40, "§a▼ 아래쪽 지도로 이동")
    );
    private static final Map<Integer, String> NAVI_SLOTS = NAVI_MAP.entrySet().stream()
            .collect(Collectors.toMap(e -> e.getValue().slot(), Map.Entry::getKey));
    private record NaviInfo(int slot, String label) {}

    public TaxiMapScreen(SHKTown plugin, TaxiMap taxiMap) {
        this.plugin = plugin;
        this.taxiMap = taxiMap;
        this.inventory = Bukkit.createInventory(this, 54, MessageUtil.parse(taxiMap.title()));

        initLayout();
    }

    private void initLayout() {
        inventory.setItem(49, GUIUtil.createItem(Material.COMPASS, "§cHOME", "§7스마트폰 메인 화면으로 복귀합니다."));

        NAVI_MAP.forEach((dir, info) -> {
            if (taxiMap.navigation().containsKey(dir)) {
                inventory.setItem(info.slot(), GUIUtil.createItem(Material.ARROW, info.label(), null));
            }
        });

        if (taxiMap.stopSlots() != null) {
            for (var entry : taxiMap.stopSlots().entrySet()) {
                int slot = entry.getKey();
                TaxiStop stop = entry.getValue();

                if (slot >= 0 && slot < 54 && stop != null) {
                    inventory.setItem(slot, GUIUtil.createItem(
                            Material.YELLOW_WOOL,
                            "§e🚕 " + stop.name(),
                            null,
                            "§fX: " + stop.position().x() + ", Y: " + stop.position().y() + ", Z: " + stop.position().z(),
                            "§7클릭시 해당 승강장으로 이동합니다."
                    ));
                }
            }
        }
    }

    @Override
    public void handleSlotClick(Player player, int slot) {
        TaxiService taxiService = plugin.getTaxiService();
        TaxiMapManager taxiMapManager = plugin.getTaxiMapManager();

        if (slot == 49) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            plugin.getSmartphoneManager().openMainScreen(player);
        }

        String direction = NAVI_SLOTS.get(slot);
        if (direction != null && taxiMap.navigation().get(direction) instanceof String nextMap) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
            taxiMapManager.openMap(player, nextMap);
            return;
        }

        TaxiStop stop = taxiMap.getStopAt(slot);
        if (stop != null) {
            Position userPosition = LocationUtil.toPosition(player.getLocation());
            if (!taxiService.isPositionInTaxiStop(userPosition)) {
                MessageUtil.send(player, taxiService.getConfig().notInStopMessage(), taxiService.getConfig().useGlobalPrefix());
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.closeInventory();
                return;
            }

            player.closeInventory();

            String mainTitleStr = taxiService.getConfig().titleMain().replace("{stop_name}", stop.name());
            String subTitleStr = taxiService.getConfig().titleSub().replace("{stop_name}", stop.name());
            MessageUtil.sendTitle(player, mainTitleStr, subTitleStr,
                                  taxiService.getConfig().titleFadeInMs(), taxiService.getConfig().titleStayMs(), taxiService.getConfig().titleFadeOutMs());
            player.playSound(player.getLocation(), Sound.ENTITY_MINECART_RIDING, 1.0f, 1.0f / ((float) taxiService.getConfig().teleportDelayTicks() / 40));

            Location targetLocation = LocationUtil.toLocation(stop.position());

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline() && targetLocation != null) {
                    player.teleport(targetLocation);
                    player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1.0f, 1.0f);

                    String arrivedMessage = taxiService.getConfig().arrivedMessage().replace("{stop_name}", stop.name());
                    MessageUtil.send(player, arrivedMessage, taxiService.getConfig().useGlobalPrefix());
                }
            }, taxiService.getConfig().teleportDelayTicks());
        }
    }

    @Override
    public void handleSlotClick(Player player, int slot, ClickType clickType) {
        handleSlotClick(player, slot);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
