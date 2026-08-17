package kr.shkworld.shktown;

import kr.shkworld.shktown.command.CommandManager;
import kr.shkworld.shktown.config.ConfigManager;
import kr.shkworld.shktown.core.navigation.service.NavigationService;
import kr.shkworld.shktown.core.taxi.service.TaxiService;
import kr.shkworld.shktown.listener.EventManager;
import kr.shkworld.shktown.ui.apps.SmartphoneManager;
import kr.shkworld.shktown.ui.apps.navigation.NavigationManager;
import kr.shkworld.shktown.ui.apps.taxi.TaxiMapManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SHKTown extends JavaPlugin {
    private ConfigManager configManager;

    private SmartphoneManager smartphoneManager;
    private TaxiService taxiService;
    private TaxiMapManager taxiMapManager;
    private NavigationService navigationService;
    private NavigationManager navigationManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.taxiService = new TaxiService();
        this.navigationService = new NavigationService();

        this.smartphoneManager = new SmartphoneManager(this);
        this.taxiMapManager = new TaxiMapManager(this);
        this.navigationManager = new NavigationManager(this);

        this.configManager = new ConfigManager(this);
        reload();

        new EventManager(this).registerEvents();
        new CommandManager(this).registerCommands();

        getLogger().info("SHK TOWN 플러그인이 성공적으로 활성화되었습니다!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SHK TOWN 플러그인이 종료되었습니다.");
    }

    public void reload() {
        configManager.loadConfigs();
    }

    public TaxiService getTaxiService() {
        return taxiService;
    }

    public NavigationService getNavigationService() {
        return navigationService;
    }

    public SmartphoneManager getSmartphoneManager() {
        return smartphoneManager;
    }

    public TaxiMapManager getTaxiMapManager() {
        return taxiMapManager;
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }
}
