package kr.shkworld.shktown.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.function.Consumer;

import static kr.shkworld.shktown.util.MessageUtil.parse;

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

        if (name != null && !name.isEmpty()) {
            meta.displayName(parse(name));
        }

        if (loreLines != null && loreLines.length > 0) {
            meta.lore(Arrays.stream(loreLines).map(MessageUtil::parse).toList());
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
