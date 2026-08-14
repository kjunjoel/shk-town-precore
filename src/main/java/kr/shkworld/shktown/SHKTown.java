package kr.shkworld.shktown;

import kr.shkworld.shktown.command.CommandManager;
import kr.shkworld.shktown.config.GlobalConfigLoader;
import kr.shkworld.shktown.config.SmartphoneConfigLoader;
import kr.shkworld.shktown.config.TaxiConfigLoader;
import kr.shkworld.shktown.core.taxi.service.TaxiService;
import kr.shkworld.shktown.core.taxi.service.TaxiServiceImpl;
import kr.shkworld.shktown.listener.EventManager;
import kr.shkworld.shktown.ui.apps.SmartphoneManager;
import kr.shkworld.shktown.ui.apps.taxi.TaxiMapManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SHKTown extends JavaPlugin {
    private SmartphoneManager smartphoneManager;
    private TaxiService taxiService;
    private TaxiMapManager taxiMapManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.taxiService = new TaxiServiceImpl();

        this.smartphoneManager = new SmartphoneManager(this);
        this.taxiMapManager = new TaxiMapManager(this);

        reloadAllConfigs();

        new EventManager(this).registerEvents();
        new CommandManager(this).registerCommands();

        getLogger().info("SHK TOWN 플러그인이 성공적으로 활성화되었습니다!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SHK TOWN 플러그인이 종료되었습니다.");
    }

    public void reloadAllConfigs() {
        reloadConfig();
        FileConfiguration config = getConfig();

        GlobalConfigLoader.loadGlobalConfig(config);

        SmartphoneConfigLoader.loadSmartphoneConfig(config, smartphoneManager);

        TaxiConfigLoader.loadSettings(config, taxiService);
        TaxiConfigLoader.loadStops(config, taxiService);
        TaxiConfigLoader.loadMaps(this, config, taxiMapManager);
    }

    public TaxiService getTaxiService() {
        return taxiService;
    }

    public SmartphoneManager getSmartphoneManager() {
        return smartphoneManager;
    }

    public TaxiMapManager getTaxiMapManager() {
        return taxiMapManager;
    }
}
