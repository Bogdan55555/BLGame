package com.bog55555dan.bLGame.menu;

import com.bog55555dan.bLGame.shop.StickShop;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public class BMenu implements Listener {
    private Inventory inv;
    private JavaPlugin plugin;
    private HashMap<ItemStack, Integer> items = new HashMap<>();
    private List<Integer> prices = new ArrayList<>();
    private String title;
    private StickShop.TypeShop typeShop;

    public BMenu(JavaPlugin plugin, StickShop.TypeShop typeShop){
        this.plugin = plugin;
        this.typeShop = typeShop;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void init() {
        FileConfiguration config = plugin.getConfig();

        String temp = "tshop";
        if (typeShop == StickShop.TypeShop.KT)
            temp = "ktshop";
        else if (typeShop == StickShop.TypeShop.ALL)
            temp = "allshop";

        ConfigurationSection ktSection = config.getConfigurationSection(temp);
        if (ktSection == null) {
            plugin.getLogger().severe("Раздел '"+ temp +"' не найден или нечитаем!");
            return;
        }

        title = config.getString(temp + ".title", "Магазин");

        prices = (List<Integer>) config.get(temp + ".prices");


        ConfigurationSection itemsSection = config.getConfigurationSection(temp + ".items");
        if (itemsSection == null) {
            plugin.getLogger().severe("Секция '" + temp + ".items' не найдена в config.yml!");
            return;
        }
        Set<String> itemsIds = itemsSection.getKeys(false);
        for (String itemId : itemsIds){
            try {
                int price = config.getInt(temp + ".items." + itemId + ".price");
                Object maxdamage = config.get(temp + ".items." + itemId + ".maxdamage");

                String materialName = config.getString(temp + ".items." + itemId + ".material");
                Material material = Material.getMaterial(materialName);
                if (material == null) {
                    plugin.getLogger().severe("Материал '" + materialName + "' не найден!");
                    continue;
                }
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();

                ConfigurationSection section = config.getConfigurationSection(temp + ".items." + itemId + ".enchantments");
                if (section != null) {
                    Set<String> enchIds = section.getKeys(false);
                    for (String enchId : enchIds) {
                        try {
                            int level = config.getInt(temp +".items." + itemId + ".enchantments." + enchId);
                            meta.addEnchant(Objects.requireNonNull(Enchantment.getByName(enchId)), level, false);
                        } catch (Exception e) {
                            plugin.getLogger().severe("CRITICAL ERROR при загрузке зачарования '" + enchId + " " + itemId + "': " + e.getClass().getSimpleName() + " - " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
                if (maxdamage != null && meta instanceof Damageable) {
                    Damageable damageable = (Damageable) meta;
                    damageable.setMaxDamage((int) maxdamage);
                }

                item.setItemMeta(meta);
                items.put(item, price);
            }
            catch (Exception e) {
                plugin.getLogger().severe("CRITICAL ERROR при загрузке предмета '" + itemId + "': " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public void open(Player player) {
        if (inv == null) {
            inv = plugin.getServer().createInventory(null, 54, title);
        }

        inv.clear();

        prices.sort(Comparator.naturalOrder());


        for (int i = 0; i < Math.min(prices.size(), 6); i++) {
            int slot = i * 9;
            inv.setItem(slot, new ItemStack(Material.GOLD_INGOT, prices.get(i)));
        }

        int itemIndex = 0;
        for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
            Integer itemPrice = entry.getValue();

            int row = -1;
            for (int j = 0; j < prices.size(); j++) {
                if (prices.get(j).equals(itemPrice)) {
                    row = j;
                    break;
                }
            }

            if (row != -1 && row < 6) {
                for (int col = 1; col <= 8; col++) {
                    int targetSlot = row * 9 + col;

                    if (targetSlot < inv.getSize() && inv.getItem(targetSlot) == null) {
                        inv.setItem(targetSlot, entry.getKey());
                        itemIndex++;
                        break;
                    }
                }
            } else {
                plugin.getLogger().warning("Цена предмета не найдена в списке: " + itemPrice);
            }
        }

        player.openInventory(inv);
    }


    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        Inventory clickedInv = event.getInventory();
        if (!clickedInv.equals(inv)) return;
        if (!event.getClickedInventory().equals(inv)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();

        if (slot % 9 == 0) {
            return;
        }

        Integer itemPrice = null;
        ItemStack purchasedItem = null;

        for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
            if (entry.getKey().isSimilar(clickedItem)) {
                itemPrice = entry.getValue();
                purchasedItem = entry.getKey();
                break;
            }
        }

        if (itemPrice == null) {
            player.sendMessage("§cЭтот предмет нельзя купить!");
            return;
        }

        int goldCount = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.getType() == Material.GOLD_INGOT) {
                goldCount += stack.getAmount();
            }
        }

        if (goldCount < itemPrice) {
            player.sendMessage("§cУ вас недостаточно золотых слитков! Требуется: " + itemPrice + ", у вас: " + goldCount);
            return;
        }

        int remainingPrice = itemPrice;
        List<ItemStack> inventoryStacks = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));

        for (int i = 0; i < inventoryStacks.size(); i++) {
            ItemStack stack = inventoryStacks.get(i);
            if (stack != null && stack.getType() == Material.GOLD_INGOT) {
                int take = Math.min(remainingPrice, stack.getAmount());
                stack.setAmount(stack.getAmount() - take);
                remainingPrice -= take;

                player.getInventory().setItem(i, stack.getAmount() > 0 ? stack : null);

                if (remainingPrice == 0) break;
            }
        }

        if (remainingPrice > 0) {
            player.sendMessage("§cНе удалось списать золото! Попробуйте снова.");
            return;
        }

        player.getInventory().addItem(purchasedItem);
        player.sendMessage("§aВы купили предмет за " + itemPrice + " золотых слитков!");

        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1.0f, 1.0f);
    }
}
