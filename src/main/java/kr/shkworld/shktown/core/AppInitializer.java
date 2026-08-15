package kr.shkworld.shktown.core;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.core.navigation.service.NavigationService;
import kr.shkworld.shktown.core.navigation.service.impl.NavigationServiceImpl;
import kr.shkworld.shktown.core.taxi.service.TaxiService;
import kr.shkworld.shktown.core.taxi.service.impl.TaxiServiceImpl;
import kr.shkworld.shktown.ui.apps.SmartphoneManager;
import kr.shkworld.shktown.ui.apps.taxi.TaxiMapManager;

public class AppInitializer {
    public static ServiceRegistry initServiceRegistry(SHKTown plugin) {
        ServiceRegistry serviceRegistry = new ServiceRegistry();

        serviceRegistry.registerService(TaxiService.class, new TaxiServiceImpl());
        serviceRegistry.registerService(NavigationService.class, new NavigationServiceImpl());

        serviceRegistry.registerService(SmartphoneManager.class, new SmartphoneManager(plugin));
        serviceRegistry.registerService(TaxiMapManager.class, new TaxiMapManager(plugin));

        return serviceRegistry;
    }
}
