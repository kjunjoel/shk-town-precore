package kr.shkworld.shktown.config;

import kr.shkworld.shktown.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;

public class GlobalConfigLoader {
    private GlobalConfigLoader() {}

    public static void loadGlobalConfig(FileConfiguration config) {
        if (config == null) return;

        String prefix = config.getString("prefix", "");
        String reloadSuccess = config.getString("reload_success", "");
        String noPermission = config.getString("no_permission", "");

        MessageUtil.initGlobalConfig(prefix, reloadSuccess, noPermission);
    }
}
