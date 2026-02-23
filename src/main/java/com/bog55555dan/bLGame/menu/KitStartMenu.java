package com.bog55555dan.bLGame.menu;

import com.bog55555dan.bLGame.KEYS.KEYS;
import com.bog55555dan.bLGame.shopItem.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitStartMenu implements Listener {
    private MenuDataInit mdInit;
    private JavaPlugin plugin;
    private String title;
    private HashMap<ItemStack, Integer> items_slot = new HashMap<>();
    private Inventory inv;
    private int setSlot = 3 * 9 - 1;

    public KitStartMenu(JavaPlugin plugin, ShopItem.Type type) {
        this.plugin = plugin;
        this.mdInit = new MenuDataInit(plugin, type);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void init() {
        mdInit.init();
        title = mdInit.getTitle();
        items_slot = mdInit.getItems_slot();
    }

    public void open(Player player) {
        if (inv == null) {
            inv = Bukkit.createInventory(null, 54, title);
        }

        inv.clear();

        for (Map.Entry<ItemStack, Integer> entry : items_slot.entrySet()) {
            inv.setItem(entry.getValue(), entry.getKey());
        }

        ItemStack setItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = setItem.getItemMeta();
        meta.setDisplayName("§aСохранить");
        meta.getPersistentDataContainer().set(KEYS.SET_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
        setItem.setItemMeta(meta);
        inv.setItem(setSlot, setItem);

        player.openInventory(inv);
    }

    public void giveKitStart(Player player) {
        for (Map.Entry<ItemStack, Integer> item : items_slot.entrySet()) {
            if (item.getKey().getType() == Material.BARRIER) {
                ItemMeta meta = item.getKey().getItemMeta();
                meta.getPersistentDataContainer().set(KEYS.KIT_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
                item.getKey().setItemMeta(meta);
            }

            boolean isArmor = false;
            switch (item.getKey().getType()) {
                case LEATHER_HELMET, CHAINMAIL_HELMET, IRON_HELMET,
                     DIAMOND_HELMET, GOLDEN_HELMET, NETHERITE_HELMET,
                     TURTLE_HELMET:
                    player.getInventory().setHelmet(item.getKey());
                    isArmor = true;
                    break;
                case LEATHER_CHESTPLATE, CHAINMAIL_CHESTPLATE, IRON_CHESTPLATE,
                     DIAMOND_CHESTPLATE, GOLDEN_CHESTPLATE, NETHERITE_CHESTPLATE:
                    player.getInventory().setChestplate(item.getKey());
                    isArmor = true;
                    break;
                case LEATHER_LEGGINGS, CHAINMAIL_LEGGINGS, IRON_LEGGINGS,
                     DIAMOND_LEGGINGS, GOLDEN_LEGGINGS, NETHERITE_LEGGINGS:
                    player.getInventory().setLeggings(item.getKey());
                    isArmor = true;
                    break;
                case LEATHER_BOOTS, CHAINMAIL_BOOTS, IRON_BOOTS,
                     DIAMOND_BOOTS, GOLDEN_BOOTS, NETHERITE_BOOTS:
                    player.getInventory().setBoots(item.getKey());
                    isArmor = true;
                    break;
            }
            if (!isArmor)
                player.getInventory().setItem(item.getValue(), item.getKey());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory openedInv = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        if (!openedInv.equals(inv)) {
            if (clickedItem.getPersistentDataContainer().has(KEYS.KIT_KEY, PersistentDataType.STRING))
                event.setCancelled(true);
            return;
        }

        if (clickedItem.getPersistentDataContainer().has(KEYS.SET_KEY)){
            player.closeInventory();
            mdInit.saveToConfig(player, inv);
        }
    }
}
