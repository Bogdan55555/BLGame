package com.bog55555dan.bLGame.KEYS;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class KEYS {
    public static NamespacedKey KT_KEY, T_KEY, ALL_KEY;

    public KEYS(JavaPlugin plugin){
        KT_KEY = new NamespacedKey(plugin, "KT_KEY");
        T_KEY = new NamespacedKey(plugin, "T_KEY");
        ALL_KEY = new NamespacedKey(plugin, "ALL_KEY");
    }
}
