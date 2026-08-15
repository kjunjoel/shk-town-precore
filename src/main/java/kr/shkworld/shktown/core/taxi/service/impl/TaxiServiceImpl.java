package kr.shkworld.shktown.core.taxi.service.impl;

import kr.shkworld.shktown.core.common.model.Position;
import kr.shkworld.shktown.core.taxi.model.TaxiConfig;
import kr.shkworld.shktown.core.taxi.model.TaxiStop;
import kr.shkworld.shktown.core.taxi.service.TaxiService;

import java.util.HashMap;
import java.util.Map;

public class TaxiServiceImpl implements TaxiService {
    private TaxiConfig taxiConfig;
    private final Map<String, TaxiStop> taxiStops = new HashMap<>();

    @Override
    public void setConfig(TaxiConfig config) {
        this.taxiConfig = config;
    }

    @Override
    public TaxiConfig getConfig() {
        return taxiConfig;
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
        if (!taxiConfig.requireTaxiStop()) {
            return true;
        }
        return getNearbyTaxiStop(position) != null;
    }

    @Override
    public TaxiStop getNearbyTaxiStop(Position position) {
        if (position == null) {
            return null;
        }

        double radiusSquared = taxiConfig.allowedRadius() * taxiConfig.allowedRadius();

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
