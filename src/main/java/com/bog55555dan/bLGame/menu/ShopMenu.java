package com.bog55555dan.bLGame.menu;

import com.bog55555dan.bLGame.KEYS.KEYS;
import com.bog55555dan.bLGame.shopItem.ShopItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public class ShopMenu implements Listener {
    private Inventory inv;
    private JavaPlugin plugin;
    private MenuDataInit mdInit;
    private HashMap<ItemStack, Integer> items = new HashMap<>();
    private List<Integer> prices = new ArrayList<>();
    private String title;
    private boolean SET;
    private int setSlot = 3 * 9 - 1;

    public ShopMenu(JavaPlugin plugin, ShopItem.Type type, boolean set){
        this.plugin = plugin;
        this.mdInit = new MenuDataInit(plugin, type);
        this.SET = set;
        title = "Магазин";
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void init() {
        mdInit.init();
        title = mdInit.getTitle();
        items = mdInit.getItems_price();
        prices = mdInit.getPrices();
    }

    public void open(Player player) {
        if (inv == null) {
            inv = plugin.getServer().createInventory(null, 54, title);
        }

        inv.clear();

        prices.sort(Comparator.naturalOrder());

        for (int i = 0; i < Math.min(prices.size(), 6); i++) {
            int slot = i * 9;
            if (prices.get(i).equals(0)) {
                for (int j = 0; j < 9; j++){
                    inv.setItem(slot + j, new ItemStack(Material.BARRIER));
                }
                continue;
            }
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

        if (SET) {
            ItemStack setItem = new ItemStack(Material.EMERALD_BLOCK);
            ItemMeta meta = setItem.getItemMeta();
            meta.setDisplayName("§aСохранить");
            meta.getPersistentDataContainer().set(KEYS.SET_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
            setItem.setItemMeta(meta);
            inv.setItem(setSlot, setItem);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        Inventory openedInv = event.getInventory();
        if (!openedInv.equals(inv)) return;

        if (!SET) {
            if (!event.getClickedInventory().equals(inv)) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();

        if (slot % 9 == 0) {
            event.setCancelled(true);
            return;
        }

        if (SET && clickedItem.getPersistentDataContainer().has(KEYS.SET_KEY)) {
            player.closeInventory();
            mdInit.saveToConfig(player, inv);
            return;
        }

        if (!SET) {
            Integer itemPrice = prices.get(slot / 9);

            if (itemPrice == null) {
                player.sendMessage("§cЭтот предмет нельзя купить!");
                return;
            }

            if (MenuDataInit.getFreeSlotsInMainInventory(player) < Math.ceil((double) clickedItem.getAmount() / 64)) {
                player.sendMessage("§cУ вас недостаточно места в инвентаре!");
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

            player.getInventory().addItem(clickedItem);
            player.sendMessage("§aВы купили предмет за " + itemPrice + " золотых слитков!");

            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1.0f, 1.0f);
        }
    }
}
