package kr.shkworld.shktown.ui.apps.taxi;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.core.taxi.model.TaxiMap;
import kr.shkworld.shktown.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TaxiMapManager {
    private final SHKTown plugin;
    private final Map<String, TaxiMap> taxiMaps = new HashMap<>();

    public TaxiMapManager(SHKTown plugin) {
        this.plugin = plugin;
    }

    public void registerMap(String id, TaxiMap map) {
        if (id != null && map != null) {
            this.taxiMaps.put(id, map);
        }
    }

    public void clearMaps() {
        this.taxiMaps.clear();
    }

    public void openMap(Player player, String id) {
        TaxiMap taxiMap = taxiMaps.get(id);
        if (taxiMap == null) {
            MessageUtil.send(player, "§c존재하지 않는 지도 타일입니다: " + id);
            return;
        }

        TaxiMapScreen screen = new TaxiMapScreen(plugin, taxiMap);
        player.openInventory(screen.getInventory());
    }
}
