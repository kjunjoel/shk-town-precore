package kr.shkworld.shktown.ui.apps;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SmartphoneManager {
    private final SHKTown plugin;
    private final Map<String, Consumer<Player>> appActions = new HashMap<>();

    private String mainTitle = "";
    private ItemStack homeButton;
    private ItemStack navigationPreviousButton;
    private ItemStack navigationNextButton;
    private List<SmartphoneApp> apps = new ArrayList<>();
    private String fixedLeftAppId = "settings";
    private String fixedRightAppId = "talk";

    public SmartphoneManager(SHKTown plugin) {
        this.plugin = plugin;
        registerDefaultApps();
    }

    private void registerDefaultApps() {
        appActions.put("taxi", player -> {
            MessageUtil.send(player, plugin.getTaxiMapManager().getLoadingAppMessage(), false);
            plugin.getTaxiMapManager().openMainScreen(player);
        });

        appActions.put("navigation", player -> {
            player.closeInventory();

            plugin.getNavigationManager().openScreen(player);
        });
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public void setHomeButton(ItemStack homeButton) {
        this.homeButton = homeButton;
    }

    public ItemStack getHomeButton() {
        return homeButton;
    }

    public void setNavigationPreviousButton(ItemStack navigationPreviousButton) {
        this.navigationPreviousButton = navigationPreviousButton;
    }

    public ItemStack getNavigationPreviousButton() {
        return navigationPreviousButton;
    }

    public void setNavigationNextButton(ItemStack navigationNextButton) {
        this.navigationNextButton = navigationNextButton;
    }

    public ItemStack getNavigationNextButton() {
        return navigationNextButton;
    }

    public void setApps(List<SmartphoneApp> apps) {
        this.apps = apps == null ? new ArrayList<>() : apps.stream()
                .sorted(Comparator.comparingInt(SmartphoneApp::order).thenComparing(SmartphoneApp::id))
                .toList();
    }

    public List<SmartphoneApp> getApps() {
        return apps;
    }

    public void setFixedAppIds(String fixedLeftAppId, String fixedRightAppId) {
        this.fixedLeftAppId = fixedLeftAppId;
        this.fixedRightAppId = fixedRightAppId;
    }

    public String getFixedLeftAppId() {
        return fixedLeftAppId;
    }

    public String getFixedRightAppId() {
        return fixedRightAppId;
    }

    public void registerAppAction(String appId, Consumer<Player> consumer) {
        appActions.put(appId, consumer);
    }

    public Consumer<Player> getAppAction(String appId) {
        return appActions.get(appId);
    }

    public void openMainScreen(Player player) {
        openMainScreen(player, 0);
    }

    public void openMainScreen(Player player, int page) {
        SmartphoneMainScreen mainScreen = new SmartphoneMainScreen(this, mainTitle, page);
        player.openInventory(mainScreen.getInventory());
    }

}
