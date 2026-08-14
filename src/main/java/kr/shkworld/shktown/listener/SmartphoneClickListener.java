package kr.shkworld.shktown.listener;

import kr.shkworld.shktown.ui.SmartphoneScreen;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SmartphoneClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof SmartphoneScreen screen) {
            event.setCancelled(true);

            if (event.getWhoClicked() instanceof Player player) {
                int rawSlot = event.getRawSlot();
                if (rawSlot >= 0 && rawSlot < event.getInventory().getSize()) {
                    screen.handleSlotClick(player, rawSlot, event.getClick());
                }
            }
        }
    }
}
