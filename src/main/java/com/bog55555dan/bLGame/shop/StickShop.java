package com.bog55555dan.bLGame.shop;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class StickShop {

    private ItemStack itemStack;
    private TypeShop typeShop;
    private static JavaPlugin plugin;

    public enum TypeShop{
        KT,
        T,
        ALL
    }

    public StickShop(JavaPlugin plugin, TypeShop typeShop, Material material, String name){
        StickShop.plugin = plugin;
        this.typeShop = typeShop;


        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return;

        meta.setDisplayName(name);
        meta.setMaxStackSize(1);

        NamespacedKey KEY = new NamespacedKey(plugin, "T_KEY");
        if (typeShop == TypeShop.KT) {
            KEY = new NamespacedKey(plugin, "KT_KEY");
        }
        else if (typeShop == TypeShop.ALL){
            KEY = new NamespacedKey(plugin, "ALL_KEY");
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
