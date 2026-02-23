package com.bog55555dan.bLGame.shopItem;

import com.bog55555dan.bLGame.KEYS.KEYS;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ShopItem {

    private ItemStack itemStack;

    public enum Type {
        KT,
        T,
        ALL,
        KIT_KT,
        KIT_T,
        MAP_CH
    }

    public ShopItem(Type typeShop, Material material, String name) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return;

        meta.setDisplayName(name);
        meta.setMaxStackSize(1);

        NamespacedKey KEY = KEYS.T_KEY;
        if (typeShop == Type.KT) {
            KEY = KEYS.KT_KEY;
        }
        else if (typeShop == Type.ALL){
            KEY = KEYS.ALL_KEY;
        }

        meta.getPersistentDataContainer().set(
                    KEY,
                    PersistentDataType.STRING,
                    UUID.randomUUID().toString()
            );

        item.setItemMeta(meta);

        itemStack = item;
    }

    public ItemStack getStickShop() {
        return itemStack;
    }
}
