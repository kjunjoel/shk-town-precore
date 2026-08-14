package kr.shkworld.shktown.core.taxi.service.impl;

import kr.shkworld.shktown.core.taxi.model.Position;
import kr.shkworld.shktown.core.taxi.model.TaxiStop;
import kr.shkworld.shktown.core.taxi.service.TaxiService;

import java.util.HashMap;
import java.util.Map;

public class TaxiServiceImpl implements TaxiService {
    private String defaultMap = "";
    private boolean requireTaxiStop = false;
    private double allowedRadius = 0.0;

    private String titleMain = "";
    private String titleSub = "";
    private int titleFadeInMs = 0;
    private int titleStayMs = 0;
    private int titleFadeOutMs = 0;
    private long teleportDelayTicks = 0;

    private boolean useGlobalPrefix = false;
    private String notInStopMessage = "";
    private String arrivedMessage = "";
    private String loadingAppMessage = "";

    private final Map<String, TaxiStop> taxiStops = new HashMap<>();

    @Override
    public void setSettings(String defaultMap, boolean requireTaxiStop, double allowedRadius) {
        this.defaultMap = defaultMap != null ? defaultMap : "";
        this.requireTaxiStop = requireTaxiStop;
        this.allowedRadius = allowedRadius;
    }

    @Override
    public String getDefaultMap() {
        return defaultMap;
    }

    @Override
    public void setUi(String titleMain, String titleSub, int titleFadeInMs, int titleStayMs, int titleFadeOutMs, long teleportDelayTicks) {
        this.titleMain = titleMain != null ? titleMain : "";
        this.titleSub = titleSub != null ? titleSub : "";
        this.titleFadeInMs = titleFadeInMs;
        this.titleStayMs = titleStayMs;
        this.titleFadeOutMs = titleFadeOutMs;
        this.teleportDelayTicks = teleportDelayTicks;
    }

    @Override
    public String getTitleMain() {
        return titleMain;
    }

    @Override
    public String getTitleSub() {
        return titleSub;
    }

    @Override
    public int getTitleFadeInMs() {
        return titleFadeInMs;
    }

    @Override
    public int getTitleStayMs() {
        return titleStayMs;
    }

    @Override
    public int getTitleFadeOutMs() {
        return titleFadeOutMs;
    }

    @Override
    public long getTeleportDelayTicks() {
        return teleportDelayTicks;
    }

    @Override
    public void setMessages(boolean useGlobalPrefix, String notInStop, String arrived, String loadingApp) {
        this.useGlobalPrefix = useGlobalPrefix;
        this.notInStopMessage = notInStop != null ? notInStop : "";
        this.arrivedMessage = arrived != null ? arrived : "";
        this.loadingAppMessage = loadingApp != null ? loadingApp : "";
    }

    @Override
    public boolean getUseGlobalPrefix() {
        return useGlobalPrefix;
    }

    @Override
    public String getNotInStopMessage() {
        return notInStopMessage;
    }

    @Override
    public String getArrivedMessage() {
        return arrivedMessage;
    }

    @Override
    public String getLoadingAppMessage() {
        return loadingAppMessage;
    }

    @Override
    public void registerTaxiStop(String key, TaxiStop taxiStop) {
        if (key != null && taxiStop != null) {
            this.taxiStops.put(key, taxiStop);
        }
    }

    @Override
    public void clearTaxiStops() {
        this.taxiStops.clear();
    }

    @Override
    public boolean isPositionInTaxiStop(Position position) {
        if (!requireTaxiStop) {
            return true;
        }
        return getNearbyTaxiStop(position) != null;
    }

    @Override
    public TaxiStop getNearbyTaxiStop(Position position) {
        if (position == null) {
            return null;
        }

        double radiusSquared = allowedRadius * allowedRadius;

        for (TaxiStop taxiStop : taxiStops.values()) {
            if (taxiStop == null || taxiStop.position() == null) {
                continue;
            }

            if (taxiStop.position().distanceSquared(position) <= radiusSquared) {
                return taxiStop;
            }
        }
        return null;
    }
}
