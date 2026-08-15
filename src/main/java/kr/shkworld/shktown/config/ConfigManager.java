package kr.shkworld.shktown.config;

import kr.shkworld.shktown.core.ServiceRegistry;
import kr.shkworld.shktown.core.navigation.service.NavigationService;
import kr.shkworld.shktown.core.taxi.service.TaxiService;
import kr.shkworld.shktown.ui.apps.SmartphoneManager;
import kr.shkworld.shktown.ui.apps.taxi.TaxiMapManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final JavaPlugin plugin;
    private final Map<String, FileConfiguration> configs = new HashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs(ServiceRegistry serviceRegistry) {
        configs.clear();

        loadConfig("global", "config.yml");

        loadConfig("smartphone", "configs/smartphone.yml");
        loadConfig("taxi", "configs/taxi.yml");
        loadConfig("navigation", "configs/navigation.yml");

        GlobalConfigLoader.loadGlobalConfig(getConfig("global"));

        SmartphoneConfigLoader.loadSmartphoneConfig(
                getConfig("smartphone"),
                serviceRegistry.getService(SmartphoneManager.class)
        );

        TaxiConfigLoader.loadTaxiConfig(
                plugin,
                getConfig("taxi"),
                serviceRegistry.getService(TaxiService.class),
                serviceRegistry.getService(TaxiMapManager.class)
        );

        NavigationConfigLoader.loadNavigationConfig(
                getConfig("navigation"),
                serviceRegistry.getService(NavigationService.class)
        );
    }

    private void loadConfig(String key, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
        }

        configs.put(key, config);
    }

    public FileConfiguration getConfig(String key) {
        return configs.get(key);
    }
}
