package kr.shkworld.shktown.listener;

import kr.shkworld.shktown.SHKTown;
import org.bukkit.plugin.PluginManager;

public class EventManager {
    private final SHKTown plugin;

    public EventManager(SHKTown plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();

        pm.registerEvents(new SmartphoneToggleListener(plugin.getSmartphoneManager()), plugin);
        pm.registerEvents(new SmartphoneClickListener(), plugin);
    }
}
