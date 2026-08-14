package kr.shkworld.shktown.config;

import kr.shkworld.shktown.core.taxi.model.Position;
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

    public static void loadSettings(FileConfiguration config, TaxiService taxiService) {
        ConfigurationSection settings = config.getConfigurationSection("taxi.settings");
        if (settings != null) {
            taxiService.setSettings(
                    settings.getString("default_map", ""),
                    settings.getBoolean("require_taxi_stop", false),
                    settings.getDouble("allowed_radius", 0.0)
            );
        }

        ConfigurationSection ui = config.getConfigurationSection("taxi.ui");
        if (ui != null) {
            taxiService.setUi(
                    ui.getString("title_main", ""),
                    ui.getString("title_sub", ""),
                    ui.getInt("title_fade_in_ms", 0),
                    ui.getInt("title_stay_ms", 0),
                    ui.getInt("title_fade_out_ms", 0),
                    ui.getLong("teleport_delay_ticks", 0L)
            );
        }
        ConfigurationSection messages =  config.getConfigurationSection("taxi.messages");
        if (messages != null) {
            taxiService.setMessages(
                    messages.getBoolean("use_global_prefix", false),
                    messages.getString("not_in_stop", ""),
                    messages.getString("arrived", ""),
                    messages.getString("loading_app", "")
            );
        }
    }

    public static void loadStops(FileConfiguration config, TaxiService taxiService) {
        taxiService.clearTaxiStops();
        ConfigurationSection stops = config.getConfigurationSection("taxi.stops");
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

    public static void loadMaps(JavaPlugin plugin, FileConfiguration config, TaxiMapManager mapManager) {
        mapManager.clearMaps();

        ConfigurationSection mapsSection = config.getConfigurationSection("taxi.maps");
        if (mapsSection == null) {
            return;
        }

        for (String id : mapsSection.getKeys(false)) {
            ConfigurationSection mapSection = mapsSection.getConfigurationSection(id);
            if (mapSection == null) continue;

            String title = mapSection.getString("title", "");

            // 네비게이션 매핑
            Map<String, String> navigation = new HashMap<>();
            ConfigurationSection navSec = mapSection.getConfigurationSection("navigation");
            if (navSec != null) {
                for (String dir : navSec.getKeys(false)) {
                    navigation.put(dir.toLowerCase(), navSec.getString(dir));
                }
            }

            // 정류장 핀 매핑 (Position DTO 적용)
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
