package kr.shkworld.shktown.core.taxi.service;

import kr.shkworld.shktown.core.common.model.Position;

import java.util.HashMap;
import java.util.Map;

public final class TaxiService {
    private double allowedRadius;
    private final Map<String, Position> taxiStops = new HashMap<>();

    public void setAllowedRadius(double allowedRadius) {
        this.allowedRadius = Math.max(0.0, allowedRadius);
    }

    public void registerTaxiStop(String key, Position position) {
        if (key != null && position != null) taxiStops.put(key, position);
    }

    public void clearTaxiStops() {
        taxiStops.clear();
    }

    public boolean isPositionInTaxiStop(Position position) {
        return getNearbyTaxiStop(position) != null;
    }

    public Position getNearbyTaxiStop(Position position) {
        if (position == null) return null;
        double radiusSquared = allowedRadius * allowedRadius;
        for (Position taxiStop : taxiStops.values()) {
            if (taxiStop.distanceSquared(position) <= radiusSquared) return taxiStop;
        }
        return null;
    }
}
