package kr.shkworld.shktown.listener.taxi;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.util.LocationUtil;
import kr.shkworld.shktown.util.TextUtil;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public final class TaxiNpcInteractionListener implements Listener {
    private final SHKTown plugin;

    public TaxiNpcInteractionListener(SHKTown plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!isConfiguredTaxiNpc(entity)) return;
        if (plugin.getTaxiService().getNearbyTaxiStop(LocationUtil.toPosition(entity.getLocation())) == null) return;

        event.setCancelled(true);
        plugin.getTaxiMapManager().openMap(event.getPlayer(), plugin.getTaxiMapManager().getDefaultMap());
    }

    private boolean isConfiguredTaxiNpc(Entity entity) {
        var taxiManager = plugin.getTaxiMapManager();
        if (!taxiManager.isNpcEnabled() || !entity.getType().name().equalsIgnoreCase(taxiManager.getNpcEntityType())) return false;
        if (taxiManager.getNpcName().isBlank()) return true;
        String name = TextUtil.plainText(entity.customName());
        return taxiManager.getNpcName().equals(name);
    }
}
