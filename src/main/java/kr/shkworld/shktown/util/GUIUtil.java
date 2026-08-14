package kr.shkworld.shktown.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GUIUtil {
    private GUIUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static ItemStack createItem(Material material, String name, String modelName, String... loreLines) {
        return buildItem(material, name, modelName, loreLines, null);
    }

    public static ItemStack createPlayerHead(Player player, String name, String... loreLines) {
        return buildItem(Material.PLAYER_HEAD, name, null, loreLines, meta -> {
           if (meta instanceof SkullMeta skullMeta) {
               skullMeta.setOwningPlayer(player);
           }
        });
    }

    private static ItemStack buildItem(Material material, String name, String modelName, String[] loreLines, Consumer<ItemMeta> customMetaModifier) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        if (customMetaModifier != null) {
            customMetaModifier.accept(meta);
        }

        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();

        if (name != null && !name.isEmpty()) {
            meta.displayName(serializer.deserialize(name));
        }

        if (loreLines != null && loreLines.length > 0) {
            List<Component> lore = new ArrayList<>();
            for (String line : loreLines) {
                lore.add(Component.text(line));
            }
            meta.lore(lore);
        }

        if (modelName != null && !modelName.isEmpty()) {
            NamespacedKey key = NamespacedKey.fromString(modelName);
            if (key != null) {
                meta.setItemModel(key);
            }
        }

        item.setItemMeta(meta);
        return item;
    }
}
