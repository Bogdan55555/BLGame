package com.bog55555dan.bLGame.menu;

import com.bog55555dan.bLGame.game.Game;
import com.bog55555dan.bLGame.shopItem.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MapChoiseMenu implements Listener {
    private JavaPlugin plugin;
    private MenuDataInit mdInit;
    private String title;
    private HashMap<ItemStack, Integer> maps = new HashMap<>();
    private String mapId = "default";
    private boolean isGol;
    private int gol_phase;
    private List<String> playersGol = new ArrayList<>();
    private Inventory inv;

    public MapChoiseMenu(JavaPlugin plugin) {
        this.plugin = plugin;
        this.mdInit = new MenuDataInit(plugin, ShopItem.Type.MAP_CH);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void init() {
        mdInit.init();
        title = mdInit.getTitle();
        maps = mdInit.getItems_slot();
        gol_phase = plugin.getConfig().getInt("game.gol_phase");
    }

    public void open (Player player) {
        if (inv == null) {
            inv = Bukkit.createInventory(null, 54, title);
        }

        for (Map.Entry<ItemStack, Integer> entry : maps.entrySet()) {
            inv.setItem(entry.getValue(), entry.getKey());
        }

        player.openInventory(inv);
    }

    public void startGol() {
        List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
        for (Player player : players) {
            open(player);
        }
        isGol = true;

        new BukkitRunnable() {
            int i = gol_phase;

            @Override
            public void run(){
                if (i <= 0) {
                    isGol = false;

                    for (Player player : players) {
                        player.sendMessage("§cВремя голосования вышло!");
                        player.closeInventory();
                    }

                    ItemStack map = null;
                    int max = 0;

                    for (Map.Entry<ItemStack, Integer> entry : maps.entrySet()) {
                        if (entry.getValue() > max) {
                            max = entry.getKey().getAmount();
                            map = entry.getKey();
                        }
                    }

                    if (map == null) {
                        mapId = "default";
                        Game.MesAll("§cНикто не голосовал!");
                        Game.MesAll("§aВыбрана карта " + mapId);

                        playersGol.clear();
                        this.cancel();
                        return;
                    }

                    Game.MesAll("§aВыбрана карта " + map.getItemMeta().getDisplayName());

                    Set<NamespacedKey> temp = map.getItemMeta().getPersistentDataContainer().getKeys();
                    mapId = temp.toArray()[0].toString().replace("blgame:", "");
                    playersGol.clear();
                    this.cancel();
                }
                i--;
            }

        }.runTaskTimer(plugin, 0, 20L);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory openedInv = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (!openedInv.equals(inv)) return;

        if (!event.getClickedInventory().equals(inv)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        if (isGol && !playersGol.contains(player.getUniqueId().toString())) {
            if (clickedItem != null && maps.containsKey(clickedItem)) {
                clickedItem.setAmount(clickedItem.getAmount() + 1);
                maps.put(clickedItem, event.getSlot());
                playersGol.add(player.getUniqueId().toString());
                player.closeInventory();
            }
        }
    }

    public String getMapId() {
        return mapId;
    }
}
