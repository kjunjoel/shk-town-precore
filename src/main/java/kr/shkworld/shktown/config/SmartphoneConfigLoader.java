package kr.shkworld.shktown.config;

import kr.shkworld.shktown.ui.apps.SmartphoneManager;
import kr.shkworld.shktown.util.GUIUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartphoneConfigLoader {
    private SmartphoneConfigLoader() {}

    public static void loadSmartphoneConfig(FileConfiguration config, SmartphoneManager smartphoneManager) {
        if (config == null || smartphoneManager == null) return;

        String title = config.getString("title", "");
        smartphoneManager.setMainTitle(title);

        Map<Integer, ItemStack> appItems = new HashMap<>();
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection == null) continue;

                int slot = itemSection.getInt("slot", 0);
                String materialString = itemSection.getString("material", "AIR");
                Material material = Material.matchMaterial(materialString);
                if (material == null) {
                    material = Material.STONE;
                }

                String name = itemSection.getString("name", "");
                String modelName = itemSection.getString("model_name", "");

                List<String> loreList = itemSection.getStringList("lore");
                ItemStack itemStack = GUIUtil.createItem(
                        material,
                        name,
                        modelName,
                        loreList.toArray(new String[0])
                );

                appItems.put(slot, itemStack);
            }
        }
        smartphoneManager.setAppItems(appItems);
    }
}
