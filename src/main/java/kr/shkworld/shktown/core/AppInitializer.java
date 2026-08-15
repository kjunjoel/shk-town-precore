package kr.shkworld.shktown.core;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.core.navigation.service.NavigationService;
import kr.shkworld.shktown.core.taxi.service.TaxiService;
import kr.shkworld.shktown.ui.apps.SmartphoneManager;
import kr.shkworld.shktown.ui.apps.taxi.TaxiMapManager;

public class AppInitializer {
    public static ServiceRegistry initServiceRegistry(SHKTown plugin) {
        ServiceRegistry serviceRegistry = new ServiceRegistry();

        serviceRegistry.registerService(TaxiService.class, plugin.getTaxiService());
        serviceRegistry.registerService(NavigationService.class, plugin.getNavigationService());

        serviceRegistry.registerService(SmartphoneManager.class, plugin.getSmartphoneManager());
        serviceRegistry.registerService(TaxiMapManager.class, plugin.getTaxiMapManager());

        return serviceRegistry;
    }
}
