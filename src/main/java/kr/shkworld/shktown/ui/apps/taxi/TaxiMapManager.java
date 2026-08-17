package kr.shkworld.shktown.ui.apps.taxi;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.core.common.model.Position;
import kr.shkworld.shktown.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TaxiMapManager {
    private final SHKTown plugin;
    private final Map<String, TaxiMapScreen> mapScreens = new HashMap<>();
    private TaxiMainScreen mainScreen;
    private ItemStack taxiCallPass;
    private String noCallPassMessage = "&c택시 호출권이 필요합니다.";
    private boolean npcEnabled;
    private String npcEntityType = "VILLAGER";
    private String npcName = "";
    private String defaultMap = "";
    private String mainScreenTitle = "";
    private Map<Integer, ItemStack> mainButtons = Map.of();
    private Map<Integer, String> mainButtonMaps = Map.of();
    private String unavailableServiceMessage = "";
    private boolean useGlobalPrefix;
    private String notInStopMessage = "";
    private String arrivedMessage = "";
    private String loadingAppMessage = "";
    private String titleMain = "";
    private String titleSub = "";
    private int titleFadeInMs;
    private int titleStayMs;
    private int titleFadeOutMs;
    private long teleportDelayTicks;

    public TaxiMapManager(SHKTown plugin) {
        this.plugin = plugin;
    }

    public void registerMap(String id, String title, Map<String, String> navigation, Map<Integer, ItemStack> stopItems,
                            Map<Integer, Position> stopPositions, Map<Integer, String> stopNames) {
        if (id != null) {
            this.mapScreens.put(id, new TaxiMapScreen(plugin, this, title, navigation, stopItems, stopPositions, stopNames));
        }
    }

    public void clearMaps() {
        this.mapScreens.clear();
        this.mainScreen = null;
    }

    public void openMap(Player player, String id) {
        TaxiMapScreen screen = mapScreens.get(id);
        if (screen == null) {
            MessageUtil.send(player, "§c존재하지 않는 지도 타일입니다: " + id);
            return;
        }

        player.openInventory(screen.getInventory());
    }

    public void configureAccess(ItemStack taxiCallPass, String noCallPassMessage,
                                boolean npcEnabled, String npcEntityType, String npcName) {
        this.taxiCallPass = taxiCallPass;
        this.noCallPassMessage = noCallPassMessage;
        this.npcEnabled = npcEnabled;
        this.npcEntityType = npcEntityType;
        this.npcName = npcName;
    }

    public void configureUi(String defaultMap, String mainScreenTitle, Map<Integer, ItemStack> mainButtons,
                            Map<Integer, String> mainButtonMaps, String unavailableServiceMessage,
                            boolean useGlobalPrefix, String notInStopMessage, String arrivedMessage, String loadingAppMessage,
                            String titleMain, String titleSub, int titleFadeInMs, int titleStayMs, int titleFadeOutMs,
                            long teleportDelayTicks) {
        this.defaultMap = defaultMap;
        this.mainScreenTitle = mainScreenTitle;
        this.mainButtons = Map.copyOf(mainButtons);
        this.mainButtonMaps = Map.copyOf(mainButtonMaps);
        this.unavailableServiceMessage = unavailableServiceMessage;
        this.useGlobalPrefix = useGlobalPrefix;
        this.notInStopMessage = notInStopMessage;
        this.arrivedMessage = arrivedMessage;
        this.loadingAppMessage = loadingAppMessage;
        this.titleMain = titleMain;
        this.titleSub = titleSub;
        this.titleFadeInMs = titleFadeInMs;
        this.titleStayMs = titleStayMs;
        this.titleFadeOutMs = titleFadeOutMs;
        this.teleportDelayTicks = teleportDelayTicks;
    }

    public boolean hasTaxiCallPass(Player player) {
        if (taxiCallPass == null) return false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(taxiCallPass)) return true;
        }
        return player.getInventory().getItemInOffHand().isSimilar(taxiCallPass);
    }

    public void sendNoCallPassMessage(Player player) {
        MessageUtil.send(player, noCallPassMessage, false);
    }

    public boolean isNpcEnabled() { return npcEnabled; }
    public String getNpcEntityType() { return npcEntityType; }
    public String getNpcName() { return npcName; }

    public void openMainScreen(Player player) {
        if (mainScreen == null) {
            mainScreen = new TaxiMainScreen(plugin, this, mainScreenTitle, mainButtons, mainButtonMaps, defaultMap, unavailableServiceMessage);
        }
        player.openInventory(mainScreen.getInventory());
    }

    public String getDefaultMap() { return defaultMap; }
    public String getLoadingAppMessage() { return loadingAppMessage; }
    public boolean isUseGlobalPrefix() { return useGlobalPrefix; }
    public String getNotInStopMessage() { return notInStopMessage; }
    public String getArrivedMessage() { return arrivedMessage; }
    public String getTitleMain() { return titleMain; }
    public String getTitleSub() { return titleSub; }
    public int getTitleFadeInMs() { return titleFadeInMs; }
    public int getTitleStayMs() { return titleStayMs; }
    public int getTitleFadeOutMs() { return titleFadeOutMs; }
    public long getTeleportDelayTicks() { return teleportDelayTicks; }
}
