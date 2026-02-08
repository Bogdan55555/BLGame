package com.bog55555dan.bLGame.commands_and_tab;

import com.bog55555dan.bLGame.Listeners.BLGameListener;
import com.bog55555dan.bLGame.shop.KEYS;
import com.bog55555dan.bLGame.shop.StickShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BLGameCommands implements CommandExecutor {

    private JavaPlugin plugin;
    private BLGameListener listener;
    private Material KT_material, T_material, All_material;
    private String KT_StickName, T_StickName, All_StickName;

    public BLGameCommands(JavaPlugin plugin, BLGameListener listener){
        plugin.getCommand("blgame").setExecutor(this);
        this.plugin = plugin;
        this.listener = listener;
        reload();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!commandSender.hasPermission("blgame.admin")){
            commandSender.sendMessage("§cУ вас нет прав! Поплач((");
            return true;
        }

        if (args.length == 0){
            helpHandle(commandSender);
            return true;
        }

        switch (args[0]){
            case "purchase":
                if (args.length < 2){
                    commandSender.sendMessage("§cТак нельзя!");
                    return true;
                }

                switch (args[1]){
                    case "start":
                        blgStartHandle(commandSender);
                        break;
                    case "stop":
                        blgStopHandle(commandSender);
                        break;
                }
                break;
            case "reload":
                reload();
                commandSender.sendMessage("§aПлагин перезагрузжен!");
                break;
        }

        return true;
    }

    private void blgStopHandle(@NotNull CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory inventory = player.getInventory();
            boolean itemsRemoved = false;

            for (int slot = 0; slot < inventory.getSize(); slot++) {
                ItemStack item = inventory.getItem(slot);

                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }

                ItemMeta meta = item.getItemMeta();
                if (meta == null) {
                    continue;
                }

                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                NamespacedKey[] keysToCheck = {
                        KEYS.KT_KEY,
                        KEYS.T_KEY,
                        KEYS.ALL_KEY
                };

                for (NamespacedKey key : keysToCheck) {
                    if (pdc.has(key, PersistentDataType.STRING)) {
                        inventory.setItem(slot, new ItemStack(Material.AIR));
                        itemsRemoved = true;
                        break;
                    }
                }
            }

            if (itemsRemoved) {
                player.sendMessage("§cВаши предметы магазина были удалены из инвентаря!");
                player.playSound(player.getLocation(), Sound.ENTITY_GHAST_WARN, 1.0f, 1.0f);
            }
        }

        sender.sendMessage("§aОбработка завершена: предметы удалены у всех игроков.");
    }


    private void blgStartHandle(@NotNull CommandSender sender) {

        for (Player player : Bukkit.getOnlinePlayers()){
            if (getTeam(player).contains("k")){
                StickShop stickShop = new StickShop(plugin, StickShop.TypeShop.KT, KT_material, KT_StickName);
                player.getInventory().addItem(stickShop.getStickShop());
            }
            else if (getTeam(player).contains("t")){
                StickShop stickShop = new StickShop(plugin, StickShop.TypeShop.T, T_material, T_StickName);
                player.getInventory().addItem(stickShop.getStickShop());
            }

            StickShop stickShop = new StickShop(plugin, StickShop.TypeShop.ALL, All_material, All_StickName);
            player.getInventory().addItem(stickShop.getStickShop());
        }

    }

    private void helpHandle(@NotNull CommandSender sender) {
        sender.sendMessage("§6--- Команды управления плагином ---");
        sender.sendMessage("§e/blgame purchase start §7—->  начать стадию закупки");
        sender.sendMessage("§e/blgame purchase stop §7—-> Закончить стадию закупки");
        sender.sendMessage("§e/blgame armorGUI §7—-> открыть меню настройки броню сторон");
        sender.sendMessage("§e/blgame ktshopGUI §7—-> открыть меню настройки магазина спецназа");
        sender.sendMessage("§e/blgame tshopGUI §7—-> открыть меню настройки магазина террористов");
        sender.sendMessage("§e/blgame shopGUI §7—-> открыть меню настройки общего магазина");
    }

    public static String getTeam(Player p){
        if(p.getScoreboard().getEntryTeam(p.getName())!=null)
            return p.getScoreboard().getEntryTeam(p.getName()).getName();
        return "";
    }

    private void reload() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        try {
            KT_material = Material.getMaterial(config.getString("ktshop.material"));
            T_material = Material.getMaterial(config.getString("tshop.material"));
            All_material = Material.getMaterial(config.getString("allshop.material"));
            KT_StickName = config.getString("ktshop.title");
            T_StickName = config.getString("tshop.title");
            All_StickName = config.getString("allshop.title");
            listener.reload();
        }
        catch (Exception e) {
            plugin.getLogger().severe("CRITICAL ERROR при перезагрузке '" + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
