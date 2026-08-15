package kr.shkworld.shktown.config;

import kr.shkworld.shktown.core.common.model.Position;
import kr.shkworld.shktown.core.taxi.model.TaxiConfig;
import kr.shkworld.shktown.core.taxi.model.TaxiMap;
import kr.shkworld.shktown.core.taxi.model.TaxiStop;
import kr.shkworld.shktown.core.taxi.service.TaxiService;
import kr.shkworld.shktown.ui.apps.taxi.TaxiMapManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class TaxiConfigLoader {
    private TaxiConfigLoader() {}

    public static void loadTaxiConfig(JavaPlugin plugin, FileConfiguration config, TaxiService taxiService, TaxiMapManager taxiMapManager) {
        if (config == null || taxiService == null || taxiMapManager == null) return;

        TaxiConfig taxiConfig = parseSettings(config);
        taxiService.setConfig(taxiConfig);

        loadStops(config, taxiService);

        loadMaps(plugin, config, taxiMapManager);
    }

    private static TaxiConfig parseSettings(FileConfiguration config) {
        ConfigurationSection settings = config.getConfigurationSection("settings");
        String defaultMap = settings != null ? settings.getString("default_map", "") : "";
        boolean requireTaxiStop = settings != null && settings.getBoolean("require_taxi_stop", false);
        double allowedRadius = settings != null ? settings.getDouble("allowed_radius", 0.0) : 0.0;
        
        ConfigurationSection ui = config.getConfigurationSection("ui");
        String titleMain = ui != null ? ui.getString("title_main", "") : "";
        String titleSub = ui != null ? ui.getString("title_sub", "") : "";
        int fadeIn = ui != null ? ui.getInt("title_fade_in_ms", 0) : 0;
        int stay = ui != null ? ui.getInt("title_stay_ms", 0) : 0;
        int fadeOut = ui != null ? ui.getInt("title_fade_out_ms", 0) : 0;
        long delayTicks = ui != null ? ui.getLong("teleport_delay_ticks", 0L) : 0L;

        ConfigurationSection messages = config.getConfigurationSection("messages");
        boolean useGlobalPrefix = messages != null && messages.getBoolean("use_global_prefix", false);
        String notInStop = messages != null ? messages.getString("not_in_stop", "") : "";
        String arrived = messages != null ? messages.getString("arrived", "") : "";
        String loadingApp = messages != null ? messages.getString("loading_app", "") : "";

        return new TaxiConfig(
                defaultMap, requireTaxiStop, allowedRadius,
                titleMain, titleSub, fadeIn, stay, fadeOut, delayTicks,
                useGlobalPrefix, notInStop, arrived, loadingApp
        );
    }

    private static void loadStops(FileConfiguration config, TaxiService taxiService) {
        if (config == null || taxiService == null) return;
        taxiService.clearTaxiStops();

        ConfigurationSection stops = config.getConfigurationSection("stops");
        if (stops != null) {
            for (String key : stops.getKeys(false)) {
                ConfigurationSection stop = stops.getConfigurationSection(key);
                if (stop == null) {
                    continue;
                }

                String name =  stop.getString("name", "");
                ConfigurationSection location = stop.getConfigurationSection("location");
                if (location != null) {
                    Position position = new Position(
                            location.getString("world"),
                            location.getDouble("x"),
                            location.getDouble("y"),
                            location.getDouble("z")
                    );
                    taxiService.registerTaxiStop(key, new TaxiStop(name, position));
                }
            }
        }
    }

    private static void loadMaps(JavaPlugin plugin, FileConfiguration config, TaxiMapManager mapManager) {
        if (config == null || mapManager == null) return;
        mapManager.clearMaps();

        ConfigurationSection mapsSection = config.getConfigurationSection("maps");
        if (mapsSection == null) {
            return;
        }

        for (String id : mapsSection.getKeys(false)) {
            ConfigurationSection mapSection = mapsSection.getConfigurationSection(id);
            if (mapSection == null) continue;

            String title = mapSection.getString("title", "");

            Map<String, String> navigation = new HashMap<>();
            ConfigurationSection navSec = mapSection.getConfigurationSection("navigation");
            if (navSec != null) {
                for (String dir : navSec.getKeys(false)) {
                    navigation.put(dir.toLowerCase(), navSec.getString(dir));
                }
            }

            Map<Integer, TaxiStop> stops = new HashMap<>();
            ConfigurationSection stopsSec = mapSection.getConfigurationSection("stops");
            if (stopsSec != null) {
                for (String slotKey : stopsSec.getKeys(false)) {
                    try {
                        int slot = Integer.parseInt(slotKey.replace("slot_", ""));
                        ConfigurationSection stopSec = stopsSec.getConfigurationSection(slotKey);
                        if (stopSec == null) continue;

                        String stopName = stopSec.getString("name");
                        ConfigurationSection locSec = stopSec.getConfigurationSection("location");
                        if (locSec != null) {
                            Position pos = new Position(
                                    locSec.getString("world", "world"),
                                    locSec.getDouble("x"),
                                    locSec.getDouble("y"),
                                    locSec.getDouble("z"),
                                    (float) locSec.getDouble("yaw", 0.0),
                                    (float) locSec.getDouble("pitch", 0.0)
                            );
                            stops.put(slot, new TaxiStop(stopName, pos));
                        }
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Parsing slot key failed in SHARK T: " + slotKey);
                    }
                }
            }

            mapManager.registerMap(id, new TaxiMap(id, title, navigation, stops));
        }
    }
}
