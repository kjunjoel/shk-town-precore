package kr.shkworld.shktown.core.navigation.service;

import kr.shkworld.shktown.core.common.model.Position;
import kr.shkworld.shktown.core.navigation.model.NavigationConfig;

import java.util.Map;

public interface NavigationService {
    void setConfig(NavigationConfig config);

    NavigationConfig getConfig();

    void registerDestination(String key, Position destination);

    void clearDestinations();

    Position getDestination(String key);

    Map<String, Position> getDestinations();

    boolean isArrived(Position current, Position destination);
}
