package kr.shkworld.shktown.listener;

import kr.shkworld.shktown.ui.apps.SmartphoneManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class SmartphoneToggleListener implements Listener {
    private final SmartphoneManager smartphoneManager;

    public SmartphoneToggleListener(SmartphoneManager smartphoneManager) {
        this.smartphoneManager = smartphoneManager;
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (player.isSneaking()) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.8f, 1.2f);

            smartphoneManager.openMainScreen(player);
        }
    }

}
