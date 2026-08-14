package kr.shkworld.shktown.ui.apps;

import kr.shkworld.shktown.SHKTown;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SmartphoneManager {
    private final SHKTown plugin;
    private final Map<Integer, Consumer<Player>> appActions = new HashMap<>();

    private String mainTitle = "";
    private Map<Integer, ItemStack> appItems = new HashMap<>();

    public SmartphoneManager(SHKTown plugin) {
        this.plugin = plugin;
        registerDefaultApps();
    }

    private void registerDefaultApps() {
        appActions.put(49, this::openMainScreen);

        appActions.put(30, player -> {
            String loadingMsg = plugin.getTaxiService().getLoadingAppMessage();
            if (loadingMsg != null) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(loadingMsg));
            }

            player.closeInventory();

            String defaultMap =  plugin.getTaxiService().getDefaultMap();
            plugin.getTaxiMapManager().openMap(player, defaultMap);
        });
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public void setAppItems(Map<Integer, ItemStack> appItems) {
        this.appItems = appItems;
    }

    public void registerAppAction(int slot, Consumer<Player> consumer) {
        appActions.put(slot, consumer);
    }

    public void openMainScreen(Player player) {
        SmartphoneMainScreen mainScreen = new SmartphoneMainScreen(player, mainTitle, appItems, appActions);
        player.openInventory(mainScreen.getInventory());
    }
}
