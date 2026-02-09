package com.bog55555dan.bLGame.shopItem;

import com.bog55555dan.bLGame.KEYS.KEYS;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class StickShop {

    private ItemStack itemStack;

    public enum TypeShop{
        KT,
        T,
        ALL
    }

    public StickShop(TypeShop typeShop, Material material, String name) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return;

        meta.setDisplayName(name);
        meta.setMaxStackSize(1);

        NamespacedKey KEY = KEYS.T_KEY;
        if (typeShop == TypeShop.KT) {
            KEY = KEYS.KT_KEY;
        }
        else if (typeShop == TypeShop.ALL){
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
