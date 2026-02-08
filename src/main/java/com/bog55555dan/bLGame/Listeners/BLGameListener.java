package com.bog55555dan.bLGame.Listeners;

import com.bog55555dan.bLGame.shop.KEYS;
import com.bog55555dan.bLGame.shop.StickShop;
import com.bog55555dan.bLGame.menu.BMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class BLGameListener implements Listener {

    private JavaPlugin plugin;
    private BMenu bMenuKT, bMenuT, bMenuAll;

    public BLGameListener(JavaPlugin plugin){
        this.plugin = plugin;
        this.bMenuKT = new BMenu(plugin, StickShop.TypeShop.KT);
        this.bMenuT = new BMenu(plugin, StickShop.TypeShop.T);
        this.bMenuAll = new BMenu(plugin, StickShop.TypeShop.ALL);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInter(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        if (meta == null) return;
        if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "KT_KEY"), PersistentDataType.STRING)){
            event.setCancelled(true);
            bMenuKT.open(player);
        }
        else if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "T_KEY"), PersistentDataType.STRING)){
            event.setCancelled(true);
            bMenuT.open(player);
        }
        else if(meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "ALL_KEY"), PersistentDataType.STRING)){
            event.setCancelled(true);
            bMenuAll.open(player);
        }
    }

    @EventHandler
    public void onCraftPrepare(PrepareItemCraftEvent event) {
        Inventory inventory = event.getInventory();

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && isForbiddenItem(item)) {

                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }
        }
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result != null && isForbiddenItem(result)) {
            event.setResult(null);
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        if (isForbiddenItem(item)) {
            event.setCancelled(true);
            event.getEnchanter().sendMessage("§cЭтот предмет нельзя зачаровывать!");
        }
    }

    private boolean isForbiddenItem(ItemStack item) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (pdc == null) return false;

        NamespacedKey[] keys = {
                KEYS.T_KEY,
                KEYS.KT_KEY,
                KEYS.ALL_KEY
        };

        for (NamespacedKey key : keys)
            if (pdc.has(key))
                return true;

        return false;
    }

    public void reload(){
        this.bMenuKT.init();
        this.bMenuT.init();
        this.bMenuAll.init();
    }
}
