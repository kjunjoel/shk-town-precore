package kr.shkworld.shktown.config;

import kr.shkworld.shktown.util.MessageUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class GlobalConfigLoader {
    private GlobalConfigLoader() {}

    public static void loadGlobalConfig(FileConfiguration config) {
        ConfigurationSection globalSection = config.getConfigurationSection("global");
        if (globalSection != null) {
            String prefix = globalSection.getString("prefix", "");
            String reloadSuccess =  globalSection.getString("reload_success", "");
            String noPermission = globalSection.getString("no_permission", "");

            MessageUtil.initGlobalConfig(prefix, reloadSuccess, noPermission);
        }
    }
}
