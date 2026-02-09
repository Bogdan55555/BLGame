package com.bog55555dan.bLGame;

import com.bog55555dan.bLGame.listeners.BLGameListener;
import com.bog55555dan.bLGame.commands_and_tab.BLGameCommands;
import com.bog55555dan.bLGame.commands_and_tab.BLGameTabComplete;
import com.bog55555dan.bLGame.KEYS.KEYS;
import org.bukkit.plugin.java.JavaPlugin;

public final class BLGame extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        new KEYS(this);
        BLGameListener blGameListener = new BLGameListener(this);
        new BLGameCommands(this, blGameListener);
        getCommand("blgame").setTabCompleter(new BLGameTabComplete());

        getLogger().info("BLGame Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BLGame Disabled!");
    }
}
