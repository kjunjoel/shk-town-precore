package kr.shkworld.shktown.listener;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.ui.apps.navigation.NavigationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final SHKTown plugin;

    public PlayerQuitListener(SHKTown plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        NavigationManager navigationManager = plugin.getNavigationManager();

        if (navigationManager.isNavigating(player)) {
            navigationManager.stopNavigation(player, false);
        }
    }
}
