package kr.shkworld.shktown.core.taxi.service;

import kr.shkworld.shktown.core.common.model.Position;
import kr.shkworld.shktown.core.taxi.model.TaxiConfig;
import kr.shkworld.shktown.core.taxi.model.TaxiStop;

public interface TaxiService {
    void setConfig(TaxiConfig config);

    TaxiConfig getConfig();

    void registerTaxiStop(String key, TaxiStop taxiStop);

    void clearTaxiStops();

    boolean isPositionInTaxiStop(Position position);

    TaxiStop getNearbyTaxiStop(Position position);
}
