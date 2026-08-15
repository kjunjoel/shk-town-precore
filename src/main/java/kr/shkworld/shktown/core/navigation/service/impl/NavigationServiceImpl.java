package kr.shkworld.shktown.core.navigation.service.impl;

import kr.shkworld.shktown.core.common.model.Position;
import kr.shkworld.shktown.core.navigation.model.NavigationConfig;
import kr.shkworld.shktown.core.navigation.service.NavigationService;

import java.util.HashMap;
import java.util.Map;

public class NavigationServiceImpl implements NavigationService {
    private NavigationConfig navigationConfig;
    private final Map<String, Position> destinations = new HashMap<>();

    @Override
    public void setConfig(NavigationConfig config) {
        this.navigationConfig = config;
    }

    @Override
    public NavigationConfig getConfig() {
        return navigationConfig;
    }

    @Override
    public void registerDestination(String key, Position destination) {
        if (key != null && destination != null) {
            this.destinations.put(key, destination);
        }
    }

    @Override
    public void clearDestinations() {
        this.destinations.clear();
    }

    @Override
    public Position getDestination(String key) {
        if (key == null) return null;

        return destinations.get(key);
    }

    @Override
    public Map<String, Position> getDestinations() {
        return destinations;
    }

    @Override
    public boolean isArrived(Position current, Position destination) {
        if (current == null || destination == null || navigationConfig == null) {
            return false;
        }

        double radiusSquared = navigationConfig.arrivedRadius() * navigationConfig.arrivedRadius();
        return current.distanceSquared(destination) <= radiusSquared;
    }

}
