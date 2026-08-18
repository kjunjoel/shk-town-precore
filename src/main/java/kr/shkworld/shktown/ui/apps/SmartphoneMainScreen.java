package kr.shkworld.shktown.ui.apps;

import kr.shkworld.shktown.ui.SmartphoneScreen;
import kr.shkworld.shktown.util.GUIUtil;
import kr.shkworld.shktown.util.MessageUtil;
import kr.shkworld.shktown.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SmartphoneMainScreen implements SmartphoneScreen {
    private static final int[] APP_SLOTS = {3, 4, 5, 12, 13, 14, 21, 22, 23, 30, 31, 32};
    private static final int PREVIOUS_PAGE_SLOT = 28;
    private static final int NEXT_PAGE_SLOT = 34;
    private static final int FIXED_LEFT_SLOT = 48;
    private static final int HOME_SLOT = 49;
    private static final int FIXED_RIGHT_SLOT = 50;

    private final SmartphoneManager smartphoneManager;
    private final Inventory inventory;
    private final Map<Integer, String> appIdsBySlot = new HashMap<>();
    private final int page;

    public SmartphoneMainScreen(SmartphoneManager smartphoneManager, String title, int page) {
        this.smartphoneManager = smartphoneManager;
        this.page = Math.max(0, page);
        this.inventory = Bukkit.createInventory(this, 54, TextUtil.parse(title));

        initLayout();
    }

    private void initLayout() {
        List<SmartphoneApp> apps = smartphoneManager.getApps();
        int startIndex = page * APP_SLOTS.length;
        for (int index = 0; index < APP_SLOTS.length && startIndex + index < apps.size(); index++) {
            SmartphoneApp app = apps.get(startIndex + index);
            inventory.setItem(APP_SLOTS[index], app.item().clone());
            appIdsBySlot.put(APP_SLOTS[index], app.id());
        }

        setFixedApp(FIXED_LEFT_SLOT, smartphoneManager.getFixedLeftAppId(), apps);
        setFixedApp(FIXED_RIGHT_SLOT, smartphoneManager.getFixedRightAppId(), apps);
        inventory.setItem(HOME_SLOT, smartphoneManager.getHomeButton() != null
                ? smartphoneManager.getHomeButton().clone()
                : GUIUtil.createItem(Material.COMPASS, "&cHOME", null));

        if (page > 0 && smartphoneManager.getNavigationPreviousButton() != null) {
            inventory.setItem(PREVIOUS_PAGE_SLOT, smartphoneManager.getNavigationPreviousButton().clone());
        }
        if (startIndex + APP_SLOTS.length < apps.size() && smartphoneManager.getNavigationNextButton() != null) {
            inventory.setItem(NEXT_PAGE_SLOT, smartphoneManager.getNavigationNextButton().clone());
        }
    }

    private void setFixedApp(int slot, String appId, List<SmartphoneApp> apps) {
        apps.stream()
                .filter(app -> app.id().equals(appId))
                .findFirst()
                .ifPresent(app -> {
                    inventory.setItem(slot, app.item().clone());
                    appIdsBySlot.put(slot, app.id());
                });
    }

    @Override
    public void handleSlotClick(Player player, int slot) {
        if (slot == PREVIOUS_PAGE_SLOT && page > 0) {
            smartphoneManager.openMainScreen(player, page - 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            return;
        }
        if (slot == NEXT_PAGE_SLOT && (page + 1) * APP_SLOTS.length < smartphoneManager.getApps().size()) {
            smartphoneManager.openMainScreen(player, page + 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            return;
        }
        if (slot == HOME_SLOT) {
            smartphoneManager.openMainScreen(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            return;
        }

        String appId = appIdsBySlot.get(slot);
        if (appId != null && smartphoneManager.getAppAction(appId) != null) {
            smartphoneManager.getAppAction(appId).accept(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            return;
        }

        ItemStack clickedItem = inventory.getItem(slot);
        if (clickedItem != null
                && clickedItem.getType() != Material.GRAY_STAINED_GLASS_PANE
                && clickedItem.getType() != Material.LIGHT_GRAY_STAINED_GLASS_PANE
                && clickedItem.getType() != Material.PLAYER_HEAD) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            MessageUtil.send(player, "§c아직 구현 중인 기능입니다.");
        }
    }

    @Override
    public void handleSlotClick(Player player, int slot, ClickType clickType) {
        handleSlotClick(player, slot);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
