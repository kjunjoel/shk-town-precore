package kr.shkworld.shktown.core.taxi.model;

import java.util.Map;

public record TaxiMap(String id, String title, Map<String, String> navigation, Map<Integer, TaxiStop> stopSlots) {

    public TaxiStop getStopAt(int slot) {
        return stopSlots.get(slot);
    }
}
