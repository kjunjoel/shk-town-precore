package kr.shkworld.shktown.ui.apps.taxi;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.core.taxi.model.Position;
import kr.shkworld.shktown.core.taxi.model.TaxiMap;
import kr.shkworld.shktown.core.taxi.model.TaxiStop;
import kr.shkworld.shktown.core.taxi.service.TaxiService;
import kr.shkworld.shktown.ui.SmartphoneScreen;
import kr.shkworld.shktown.util.GUIUtil;
import kr.shkworld.shktown.util.LocationUtil;
import kr.shkworld.shktown.util.MessageUtil;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class TaxiMapScreen implements SmartphoneScreen {
    private final SHKTown plugin;
    private final TaxiMap taxiMap;
    private final Inventory inventory;

    public TaxiMapScreen(SHKTown plugin, TaxiMap taxiMap) {
        this.plugin = plugin;
        this.taxiMap = taxiMap;
        this.inventory = Bukkit.createInventory(this, 54, MessageUtil.parse(taxiMap.title()));

        initLayout();
    }

    private void initLayout() {
        inventory.setItem(49, GUIUtil.createItem(Material.COMPASS, "§c§lHOME", "§7스마트폰 메인 화면으로 복귀합니다."));

        if (taxiMap.navigation().containsKey("west")) {
            inventory.setItem(18, GUIUtil.createItem(Material.ARROW, "§a◀ 서쪽 지도로 이동", null));
        }
        if (taxiMap.navigation().containsKey("east")) {
            inventory.setItem(26, GUIUtil.createItem(Material.ARROW, "§a동쪽 지도로 이동 ▶", null));
        }

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
            plugin.getSmartphoneManager().openMainScreen(player);
        }

        if (slot == 18 && taxiMap.navigation().containsKey("west")) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
            taxiMapManager.openMap(player, taxiMap.navigation().get("west"));
            return;
        }

        if (slot == 26 && taxiMap.navigation().containsKey("east")) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
            taxiMapManager.openMap(player, taxiMap.navigation().get("east"));
            return;
        }

        TaxiStop stop = taxiMap.getStopAt(slot);
        if (stop != null) {
            Position userPosition = LocationUtil.toPosition(player.getLocation());
            if (!taxiService.isPositionInTaxiStop(userPosition)) {
                MessageUtil.send(player, taxiService.getNotInStopMessage(), taxiService.getUseGlobalPrefix());
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.closeInventory();
                return;
            }

            player.closeInventory();

            String mainTitleStr = taxiService.getTitleMain().replace("{stop_name}", stop.name());
            String subTitleStr = taxiService.getTitleSub().replace("{stop_name}", stop.name());
            MessageUtil.sendTitle(player, mainTitleStr, subTitleStr,
                                  taxiService.getTitleFadeInMs(), taxiService.getTitleStayMs(), taxiService.getTitleFadeOutMs());
            player.playSound(player.getLocation(), Sound.ENTITY_MINECART_RIDING, 1.0f, 1.0f);

            Location targetLocation = LocationUtil.toLocation(stop.position());

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline() && targetLocation != null) {
                    player.teleport(targetLocation);
                    player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1.0f, 1.0f);

                    String arrivedMessage = taxiService.getArrivedMessage().replace("{stop_name}", stop.name());
                    MessageUtil.send(player, arrivedMessage, taxiService.getUseGlobalPrefix());
                }
            }, taxiService.getTeleportDelayTicks());
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
