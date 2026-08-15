package kr.shkworld.shktown.config;

import kr.shkworld.shktown.core.common.model.Position;
import kr.shkworld.shktown.core.navigation.model.NavigationConfig;
import kr.shkworld.shktown.core.navigation.service.NavigationService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class NavigationConfigLoader {
    private NavigationConfigLoader() {}

    public static void loadNavigationConfig(FileConfiguration config, NavigationService navigationService) {
        if (config == null ||  navigationService == null) return;

        NavigationConfig navigationConfig = parseSettings(config);
        navigationService.setConfig(navigationConfig);

        loadDestinations(config, navigationService);
    }

    private static NavigationConfig parseSettings(FileConfiguration config) {
        ConfigurationSection settings = config.getConfigurationSection("settings");
        String title = settings != null ? settings.getString("title", "") : "";
        String betterhudPointerId = settings != null ? settings.getString("better_hud_pointer_id", "") : "";
        double arrivalRadius = settings != null ? settings.getDouble("arrival_radius", 0.0) : 0.0;
        int updateIntervalTicks = settings != null ? settings.getInt("update_interval_ticks", 0) : 0;

        ConfigurationSection messages = config.getConfigurationSection("messages");
        boolean useGlobalPrefix = messages != null && messages.getBoolean("use_global_prefix", false);
        String started = messages != null ? messages.getString("started", "") : "";
        String stopped = messages != null ? messages.getString("stopped", "") : "";
        String arrived = messages != null ? messages.getString("arrived", "") : "";
        String differentWorld =  messages != null ? messages.getString("different_world", "") : "";

        return new NavigationConfig(
                title, betterhudPointerId, arrivalRadius, updateIntervalTicks,
                useGlobalPrefix, started, stopped, arrived, differentWorld
        );
    }

    private static void loadDestinations(FileConfiguration config, NavigationService navigationService) {
        if (config == null || navigationService == null) return;
        navigationService.clearDestinations();

        ConfigurationSection destinations = config.getConfigurationSection("destinations");
        if (destinations != null) {
            for (String key : destinations.getKeys(false)) {
                ConfigurationSection destination = destinations.getConfigurationSection(key);
                if (destination == null) {
                    continue;
                }

                String name = destination.getString("name", "");
                ConfigurationSection location = destination.getConfigurationSection("location");
                if (location != null) {
                    navigationService.registerDestination(name, new Position(
                            location.getString("world"),
                            location.getDouble("x"),
                            location.getDouble("y"),
                            location.getDouble("z")
                    ));
                }
            }
        }
    }
}
