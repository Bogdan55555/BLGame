package com.bog55555dan.bLGame.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;


public class MapLocData {
    private String id;
    private Location pur_kt_loc, pur_t_loc, game_kt_loc, game_t_loc;
    private JavaPlugin plugin;
    private World world;

    public MapLocData(JavaPlugin plugin, String id) {
        this.id = id;
        this.plugin = plugin;

        world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("maps." + id + ".world")));
        pur_kt_loc = getLocationFromList("maps." + id + ".pur_kt_loc");
        pur_t_loc = getLocationFromList("maps." + id + ".pur_t_loc");
        game_kt_loc = getLocationFromList("maps." + id + ".game_kt_loc");
        game_t_loc = getLocationFromList("maps." + id + ".game_t_loc");
    }

    private Location getLocationFromList(String path) {
        FileConfiguration config = plugin.getConfig();
        List<Integer> coords = config.getIntegerList(path);
        if (coords == null || coords.size() < 3) {
            plugin.getLogger().warning("Некорректные координаты по пути: " + path);
            return null;
        }
        double x = coords.get(0);
        double y = coords.get(1);
        double z = coords.get(2);

        return new Location(world, x, y, z);
    }

    public String getId() {
        return id;
    }

    public Location getGame_kt_loc() {
        return game_kt_loc;
    }

    public Location getGame_t_loc() {
        return game_t_loc;
    }

    public Location getPur_kt_loc() {
        return pur_kt_loc;
    }

    public Location getPur_t_loc() {
        return pur_t_loc;
    }
}
