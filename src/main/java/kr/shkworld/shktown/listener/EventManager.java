package kr.shkworld.shktown.listener;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.listener.player.PlayerQuitListener;
import kr.shkworld.shktown.listener.smartphone.SmartphoneClickListener;
import kr.shkworld.shktown.listener.smartphone.SmartphoneToggleListener;
import kr.shkworld.shktown.listener.taxi.TaxiNpcInteractionListener;
import org.bukkit.plugin.PluginManager;

public class EventManager {
    private final SHKTown plugin;

    public EventManager(SHKTown plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();

        pm.registerEvents(new PlayerQuitListener(plugin), plugin);
        pm.registerEvents(new SmartphoneToggleListener(plugin.getSmartphoneManager()), plugin);
        pm.registerEvents(new SmartphoneClickListener(), plugin);
        pm.registerEvents(new TaxiNpcInteractionListener(plugin), plugin);
    }
}
