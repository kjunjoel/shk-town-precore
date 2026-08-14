package kr.shkworld.shktown.core.taxi.service;

import kr.shkworld.shktown.core.taxi.model.Position;
import kr.shkworld.shktown.core.taxi.model.TaxiStop;

public interface TaxiService {
    void setSettings(String defaultMap, boolean requireTaxiStop, double allowedRadius);

    String getDefaultMap();

    void setUi(String titleMain, String titleSub, int titleFadeInMs, int titleStayMs, int titleFadeOutMs, long teleportDelayTicks);

    String getTitleMain();

    String getTitleSub();

    int getTitleFadeInMs();

    int getTitleStayMs();

    int getTitleFadeOutMs();

    long getTeleportDelayTicks();

    void setMessages(boolean useGlobalPrefix, String notInStop, String arrived, String loadingApp);

    boolean getUseGlobalPrefix();

    String getNotInStopMessage();

    String getArrivedMessage();

    String getLoadingAppMessage();

    void registerTaxiStop(String key, TaxiStop taxiStop);

    void clearTaxiStops();

    boolean isPositionInTaxiStop(Position position);

    TaxiStop getNearbyTaxiStop(Position position);
}
