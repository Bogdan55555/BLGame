//package com.bog55555dan.bLGame.game;
//
//import com.bog55555dan.bLGame.KEYS.KEYS;
//import com.bog55555dan.bLGame.menu.KitStartMenu;
//import com.bog55555dan.bLGame.menu.MapChoiseMenu;
//import com.bog55555dan.bLGame.shopItem.ShopItem;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.format.TextColor;
//import net.kyori.adventure.title.TitlePart;
//import org.apache.commons.lang3.function.FailableLongSupplier;
//import org.bukkit.*;
//import org.bukkit.configuration.ConfigurationSection;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.Inventory;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//import org.bukkit.persistence.PersistentDataContainer;
//import org.bukkit.persistence.PersistentDataType;
//import org.bukkit.plugin.java.JavaPlugin;
//import org.bukkit.scheduler.BukkitRunnable;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//
//
//public class Game {
//    private JavaPlugin plugin;
//    private KitStartMenu kitStartMenuKT, kitStartMenuT;
//    private MapChoiseMenu mapChoiseMenu;
//    private int pur_phase, gol_phase;
//    private Random random = new Random();
//    private String mapId, KT_StickName, T_StickName, All_StickName;
//    private Material KT_material, T_material, All_material;
//    private List<MapLocData> mapsLocs = new ArrayList<>();
//
//    public Game(JavaPlugin plugin) {
//        this.plugin = plugin;
//        kitStartMenuKT = new KitStartMenu(plugin, ShopItem.Type.KIT_KT);
//        kitStartMenuT = new KitStartMenu(plugin, ShopItem.Type.KIT_T);
//        mapChoiseMenu = new MapChoiseMenu(plugin);
//        reload();
//    }
//
//    public void reload() {
//        FileConfiguration config = plugin.getConfig();
//
//        KT_material = Material.getMaterial(config.getString("ktshop.material"));
//        T_material = Material.getMaterial(config.getString("tshop.material"));
//        All_material = Material.getMaterial(config.getString("allshop.material"));
//        KT_StickName = config.getString("ktshop.title");
//        T_StickName = config.getString("tshop.title");
//        All_StickName = config.getString("allshop.title");
//        kitStartMenuKT.init();
//        kitStartMenuT.init();
//        mapChoiseMenu.init();
//
//        pur_phase = config.getInt("game.pur_phase");
//        gol_phase = plugin.getConfig().getInt("game.gol_phase");
//
//        mapsLocs.clear();
//        ConfigurationSection section = config.getConfigurationSection("maps");
//        if (section != null) {
//            Set<String> mapsIds = section.getKeys(false);
//            for (String mapId : mapsIds) {
//                MapLocData temp = new MapLocData(plugin, mapId);
//                mapsLocs.add(temp);
//            }
//        }
//    }
//
//    public void gameStart() {
//        List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
//        for (Player player : players) {
//            if (getTeam(player).contains("kt")) {
//                kitStartMenuKT.giveKitStart(player);
//                player.sendMessage("§aВам выдали китстарт КТ!");
//            }
//            else if (getTeam(player).contains("t")){
//                kitStartMenuT.giveKitStart(player);
//                player.sendMessage("§aВам выдали китстарт Т!");
//            }
//        }
//
//        mapChoiseMenu.startGol();
//
//        new BukkitRunnable() {
//            int i = gol_phase + 2;
//
//            @Override
//            public void run() {
//                if (i <= 0) {
//                    mapId = mapChoiseMenu.getMapId();
//                    purStart();
//                    this.cancel();
//                }
//                i--;
//            }
//
//        }.runTaskTimer(plugin, 0, 20L);
//    }
//
//    public void gameStop() {}
//
//    private void purStop() {
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            Inventory inventory = player.getInventory();
//            boolean itemsRemoved = false;
//
//            for (int slot = 0; slot < inventory.getSize(); slot++) {
//                ItemStack item = inventory.getItem(slot);
//
//                if (item == null || item.getType() == Material.AIR) {
//                    continue;
//                }
//
//                ItemMeta meta = item.getItemMeta();
//                if (meta == null) {
//                    continue;
//                }
//
//                PersistentDataContainer pdc = meta.getPersistentDataContainer();
//                NamespacedKey[] keysToCheck = {
//                        KEYS.KT_KEY,
//                        KEYS.T_KEY,
//                        KEYS.ALL_KEY
//                };
//
//                for (NamespacedKey key : keysToCheck) {
//                    if (pdc.has(key, PersistentDataType.STRING)) {
//                        inventory.setItem(slot, new ItemStack(Material.AIR));
//                        itemsRemoved = true;
//                        break;
//                    }
//                }
//            }
//
//            if (itemsRemoved) {
//                player.sendMessage("§cВаши предметы магазина были удалены из инвентаря!");
//                player.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 1.0f, 1.0f);
//            }
//        }
//    }
//
//    private void purStart() {
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            if (getTeam(player).contains("kt")) {
//                ShopItem shopItem = new ShopItem(ShopItem.Type.KT, KT_material, KT_StickName);
//                player.getInventory().addItem(shopItem.getStickShop());
//            } else if (getTeam(player).contains("t")) {
//                ShopItem shopItem = new ShopItem(ShopItem.Type.T, T_material, T_StickName);
//                player.getInventory().addItem(shopItem.getStickShop());
//            }
//
//            ShopItem shopItem = new ShopItem(ShopItem.Type.ALL, All_material, All_StickName);
//            player.getInventory().addItem(shopItem.getStickShop());
//            player.sendMessage("§aВам выдали предметы магазина!");
//            player.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 1.0f, 1.0f);
//        }
//        titlePurDisplay();
//    }
//
//    private void titlePurDisplay() {
//        List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
//
//        new BukkitRunnable() {
//            private int i = pur_phase;
//
//            @Override
//            public void run() {
//                try {
//                    if (i <= 0) {
//                        Component title = Component.text("Подготовка окончена!")
//                                .color(TextColor.color(255, 0, 0));
//
//                        purStop();
//
//                        for (Player player : players) {
//                            player.sendTitlePart(
//                                    TitlePart.TITLE,
//                                    title
//                            );
//                            tpPlayerToGameMap(player);
//                        }
//
//                        this.cancel();
//                        return;
//                    }
//
//                    int r = random.nextInt(0, 256);
//                    int g = random.nextInt(0, 256);
//                    int b = random.nextInt(0, 256);
//
//                    Component title = Component.text(String.valueOf(i))
//                            .color(TextColor.color(r, g, b));
//
//                    for (Player player : players)
//                        player.sendTitlePart(
//                               TitlePart.TITLE,
//                               title
//                        );
//
//                    i--;
//
//                } catch (Exception e) {
//                    plugin.getLogger().warning(
//                            "Ошибка в таймере " + e.getMessage()
//                    );
//                    this.cancel();
//                }
//            }
//        }.runTaskTimer(plugin, 0, 20L);
//    }
//
//
//    private void tpPlayerToGameMap(Player player) {
//        try {
//            if (mapId != null) {
//                for (MapLocData mapLocData : mapsLocs) {
//                    if (mapLocData.getId().equals(mapId)) {
//                        Location loc = (getTeam(player).contains("kt")) ?
//                                mapLocData.getGame_kt_loc() : mapLocData.getGame_t_loc();
//
//                        if (loc == null) {
//                            plugin.getLogger().warning("Location для " + player.getName() + " (" + getTeam(player) + ") is null!");
//                            player.sendMessage("§cОшибка: не найдена локация карты для вашей команды!");
//                            return;
//                        }
//                        if (loc.getWorld() == null) {
//                            plugin.getLogger().warning("World is null для " + player.getName() + " (map: " + mapId + ")");
//                            player.sendMessage("§cОшибка: не найден мир для карты!");
//                            return;
//                        }
//
//                        plugin.getLogger().info("TP " + player.getName() + " to " + loc);
//                        player.teleport(loc);
//                        player.sendMessage(getTeam(player).contains("kt") ? "§bВы телепортированы на карту!" : "§6Вы телепортированы на карту!");
//                        return;
//                    }
//                }
//                plugin.getLogger().warning("Не найдена карта с id=" + mapId);
//            } else {
//                plugin.getLogger().warning("mapId == null");
//            }
//        } catch (Exception e) {
//            plugin.getLogger().severe("CRITICAL ERROR при телепортации '" + mapId + "': " + e.getClass().getSimpleName() + " - " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public void openKitMenu(Player player, ShopItem.Type type) {
//        if (type == ShopItem.Type.KT) {
//            kitStartMenuKT.open(player);
//        }
//        else if (type == ShopItem.Type.T) {
//            kitStartMenuT.open(player);
//        }
//    }
//
//    public static String getTeam(Player p){
//        if(p.getScoreboard().getEntryTeam(p.getName())!=null)
//            return p.getScoreboard().getEntryTeam(p.getName()).getName();
//        return "";
//    }
//
//    public static void MesAll(String mes) {
//        for (Player player: Bukkit.getOnlinePlayers()){
//            player.sendMessage(mes);
//        }
//    }
//
//    public Material getKT_material() {
//        return KT_material;
//    }
//
//    public Material getT_material() {
//        return T_material;
//    }
//
//    public Material getAll_material() {
//        return All_material;
//    }
//
//    public String getKT_StickName() {
//        return KT_StickName;
//    }
//
//    public String getT_StickName() {
//        return T_StickName;
//    }
//
//    public String getAll_StickName() {
//        return All_StickName;
//    }
//}
package com.bog55555dan.bLGame.game;

import com.bog55555dan.bLGame.KEYS.KEYS;
import com.bog55555dan.bLGame.menu.KitStartMenu;
import com.bog55555dan.bLGame.menu.MapChoiseMenu;
import com.bog55555dan.bLGame.shopItem.ShopItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.*;

public class Game implements Listener {
    private JavaPlugin plugin;
    private KitStartMenu kitStartMenuKT, kitStartMenuT;
    private MapChoiseMenu mapChoiseMenu;
    private int pur_phase, gol_phase;
    private Random random = new Random();
    private String mapId, KT_StickName, T_StickName, All_StickName;
    private Material KT_material, T_material, All_material;
    private List<MapLocData> mapsLocs = new ArrayList<>();

    private int currentRound = 0;
    private int maxRounds = 12;
    private int scoreKT = 0;
    private int scoreT = 0;
    private boolean roundActive = false;

    public Game(JavaPlugin plugin) {
        this.plugin = plugin;

        kitStartMenuKT = new KitStartMenu(plugin, ShopItem.Type.KIT_KT);
        kitStartMenuT = new KitStartMenu(plugin, ShopItem.Type.KIT_T);
        mapChoiseMenu = new MapChoiseMenu(plugin);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        reload();
    }

    public void reload() {
        FileConfiguration config = plugin.getConfig();

        KT_material = Material.getMaterial(config.getString("ktshop.material"));
        T_material = Material.getMaterial(config.getString("tshop.material"));
        All_material = Material.getMaterial(config.getString("allshop.material"));
        KT_StickName = config.getString("ktshop.title");
        T_StickName = config.getString("tshop.title");
        All_StickName = config.getString("allshop.title");
        kitStartMenuKT.init();
        kitStartMenuT.init();
        mapChoiseMenu.init();

        pur_phase = config.getInt("game.pur_phase");
        gol_phase = config.getInt("game.gol_phase");
        maxRounds = config.getInt("game.max_rounds", 12);

        mapsLocs.clear();
        ConfigurationSection section = config.getConfigurationSection("maps");
        if (section != null) {
            Set<String> mapsIds = section.getKeys(false);
            for (String mapId : mapsIds) {
                MapLocData temp = new MapLocData(plugin, mapId);
                mapsLocs.add(temp);
            }
        }
    }

    public void gameStart() {
        currentRound = 0;
        scoreKT = 0;
        scoreT = 0;
        roundActive = false;

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player player : players) {
            if (getTeam(player).contains("kt")) {
                kitStartMenuKT.giveKitStart(player);
                player.sendMessage("§aВам выдали китстарт КТ!");
            } else if (getTeam(player).contains("t")){
                kitStartMenuT.giveKitStart(player);
                player.sendMessage("§aВам выдали китстарт Т!");
            }
        }

        mapChoiseMenu.startGol();

        showTitleAll(
                Component.text("Начало игры!").color(TextColor.color(80,245,66)),
                Component.text("Проголосуйте за карту...").color(NamedTextColor.YELLOW),
                1, 40, 20
        );

        new BukkitRunnable() {
            int i = gol_phase + 2;
            @Override
            public void run() {
                if (i <= 0) {
                    mapId = mapChoiseMenu.getMapId();
                    purStart();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            startNextRound();
                        }
                    }.runTaskLater(plugin, pur_phase * 20L + 40L);
                    this.cancel();
                }
                i--;
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    public void gameStop() {
        roundActive = false;
        showTitleAll(
                Component.text("§cИгра остановлена!"),
                Component.text("Это конец, спасибо за игру!"),
                1, 40, 20
        );
    }

    private void purStop() {
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
                player.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 1.0f, 1.0f);
            }
        }
    }

    private void purStart() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (getTeam(player).contains("kt")) {
                ShopItem shopItem = new ShopItem(ShopItem.Type.KT, KT_material, KT_StickName);
                player.getInventory().addItem(shopItem.getStickShop());
            } else if (getTeam(player).contains("t")) {
                ShopItem shopItem = new ShopItem(ShopItem.Type.T, T_material, T_StickName);
                player.getInventory().addItem(shopItem.getStickShop());
            }

            ShopItem shopItem = new ShopItem(ShopItem.Type.ALL, All_material, All_StickName);
            player.getInventory().addItem(shopItem.getStickShop());
            player.sendMessage("§aВам выдали предметы магазина!");
            player.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 1.0f, 1.0f);
        }
        titlePurDisplay();
    }

    private void titlePurDisplay() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        new BukkitRunnable() {
            private int i = pur_phase;
            @Override
            public void run() {
                try {
                    if (i <= 0) {
                        Component title = Component.text("Подготовка окончена!").color(TextColor.color(255, 0, 0));
                        purStop();

                        for (Player player : players) {
                            player.sendTitlePart(TitlePart.TITLE, title);
                        }
                        this.cancel();
                        return;
                    }

                    int r = random.nextInt(0, 256);
                    int g = random.nextInt(0, 256);
                    int b = random.nextInt(0, 256);

                    Component title = Component.text("⏳До старта: " + i)
                            .color(TextColor.color(r, g, b));
                    Component sub = Component.text("Будет Раунд " + (currentRound + 1) + " / " + maxRounds)
                            .color(NamedTextColor.YELLOW);

                    for (Player player : players) {
                        player.sendTitlePart(TitlePart.TITLE, title);
                        player.sendTitlePart(TitlePart.SUBTITLE, sub);
                    }
                    i--;
                } catch (Exception e) {
                    plugin.getLogger().warning("Ошибка в таймере " + e.getMessage());
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    private void tpPlayerToGameMap(Player player) {
        try {
            if (mapId != null) {
                for (MapLocData mapLocData : mapsLocs) {
                    if (mapLocData.getId().equals(mapId)) {
                        Location loc = (getTeam(player).contains("kt")) ?
                                mapLocData.getGame_kt_loc() : mapLocData.getGame_t_loc();

                        if (loc == null) {
                            plugin.getLogger().warning("Location для " + player.getName() + " (" + getTeam(player) + ") is null!");
                            player.sendMessage("§cОшибка: не найдена локация карты для вашей команды!");
                            return;
                        }
                        if (loc.getWorld() == null) {
                            plugin.getLogger().warning("World is null для " + player.getName() + " (map: " + mapId + ")");
                            player.sendMessage("§cОшибка: не найден мир для карты!");
                            return;
                        }

                        plugin.getLogger().info("TP " + player.getName() + " to " + loc);
                        player.teleport(loc);
                        player.sendMessage(getTeam(player).contains("kt") ? "§bВы телепортированы на карту!" : "§6Вы телепортированы на карту!");
                        return;
                    }
                }
                plugin.getLogger().warning("Не найдена карта с id=" + mapId);
            } else {
                plugin.getLogger().warning("mapId == null");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("CRITICAL ERROR при телепортации '" + mapId + "': " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void tpPlayerToPurLoc(Player player) {
        try {
            if (mapId != null) {
                for (MapLocData mapLocData : mapsLocs) {
                    if (mapLocData.getId().equals(mapId)) {
                        Location loc = (getTeam(player).contains("kt")) ?
                                mapLocData.getPur_kt_loc() : mapLocData.getPur_t_loc();

                        if (loc == null) {
                            plugin.getLogger().warning("Location для " + player.getName() + " (" + getTeam(player) + ") is null!");
                            player.sendMessage("§cОшибка: не найдена локация закупки для вашей команды!");
                            return;
                        }
                        if (loc.getWorld() == null) {
                            plugin.getLogger().warning("World is null для " + player.getName() + " (map: " + mapId + ")");
                            player.sendMessage("§cОшибка: не найден мир для карты!");
                            return;
                        }

                        plugin.getLogger().info("TP " + player.getName() + " to " + loc);
                        player.teleport(loc);
                        player.sendMessage(getTeam(player).contains("kt") ? "§bВы телепортированы на локацию закупки!" : "§6Вы телепортированы на локацию закупки!");
                        return;
                    }
                }
                plugin.getLogger().warning("Не найдена карта с id=" + mapId);
            } else {
                plugin.getLogger().warning("mapId == null");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("CRITICAL ERROR при телепортации '" + mapId + "': " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void openKitMenu(Player player, ShopItem.Type type) {
        if (type == ShopItem.Type.KT) {
            kitStartMenuKT.open(player);
        } else if (type == ShopItem.Type.T) {
            kitStartMenuT.open(player);
        }
    }

    public static String getTeam(Player p) {
        if (p.getScoreboard().getEntryTeam(p.getName()) != null)
            return p.getScoreboard().getEntryTeam(p.getName()).getName();
        return "";
    }

    public static void MesAll(String mes) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(mes);
        }
    }

    public Material getKT_material() {
        return KT_material;
    }

    public Material getT_material() {
        return T_material;
    }

    public Material getAll_material() {
        return All_material;
    }

    public String getKT_StickName() {
        return KT_StickName;
    }

    public String getT_StickName() {
        return T_StickName;
    }

    public String getAll_StickName() {
        return All_StickName;
    }

    public void startNextRound() {
        if (currentRound >= maxRounds) {
            endGame();
            return;
        }
        currentRound++;
        roundActive = true;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isDead()) p.spigot().respawn();
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.setFireTicks(0);
            if (getTeam(p).contains("kt")) kitStartMenuKT.giveKitStart(p);
            else if (getTeam(p).contains("t")) kitStartMenuT.giveKitStart(p);
            tpPlayerToGameMap(p);

            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 2.0f);
        }

        showTitleAll(
                Component.text("Раунд " + currentRound + " / " + maxRounds).color(NamedTextColor.AQUA),
                Component.text("Счет: " + scoreKT + "§7 : §c" + scoreT).color(NamedTextColor.GOLD),
                10, 50, 10
        );
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        checkVictoryCondition();
    }

    public void checkVictoryCondition() {
        if (!roundActive) return;
        boolean allKTDead = true;
        boolean allTDead = true;
        for (Player p : Bukkit.getOnlinePlayers()) {
            String team = getTeam(p);
            if (team.contains("kt") && !p.isDead()) allKTDead = false;
            if (team.contains("t") && !p.isDead()) allTDead = false;
        }

        if (allKTDead && !allTDead) {
            scoreT++;
            roundActive = false;
            showTitleAll(
                    Component.text("ПОБЕДА ТЕРРОРИСТОВ!").color(TextColor.color(245, 65, 65)),
                    Component.text("Террористы уничтожили КТ").color(NamedTextColor.RED),
                    10, 45, 30
            );
            showScoreBoard();
            new BukkitRunnable() {
                @Override
                public void run() {
                    finishRound();
                }
            }.runTaskLater(plugin, 45L);

        } else if (allTDead && !allKTDead) {
            scoreKT++;
            roundActive = false;
            showTitleAll(
                    Component.text("ПОБЕДА КОНТР-ТЕРРОРИСТОВ!").color(TextColor.color(65, 152, 245)),
                    Component.text("КТ уничтожили террористов").color(NamedTextColor.BLUE),
                    10, 45, 30
            );
            showScoreBoard();
            new BukkitRunnable() {
                @Override
                public void run() {
                    finishRound();
                }
            }.runTaskLater(plugin, 45L);
        }
    }

    public void finishRound() {
        if (currentRound >= maxRounds) {
            endGame();
        } else {
            purStart();
            new BukkitRunnable() {
                @Override
                public void run() {
                    startNextRound();
                }
            }.runTaskLater(plugin, pur_phase * 20L + 40L);
        }
    }

    public void endGame() {
        roundActive = false;
        String winMsg;
        if (scoreKT > scoreT) {
            winMsg = "КОНТР-ТЕРРОРИСТЫ ПОБЕЖДАЮТ!";
        } else if (scoreT > scoreKT) {
            winMsg = "ТЕРРОРИСТЫ ПОБЕЖДАЮТ!";
        } else {
            winMsg = "НИЧЬЯ!";
        }
        showTitleAll(
                Component.text("ИГРА ОКОНЧЕНА").color(NamedTextColor.GOLD),
                Component.text(winMsg + "  §b" + scoreKT + "§7:§c" + scoreT).color(NamedTextColor.WHITE),
                20, 60, 40
        );
        MesAll("§6Игра окончена!\n§f" + winMsg + " §b" + scoreKT + "§7:§c" + scoreT);
        currentRound = 0;
        scoreKT = 0;
        scoreT = 0;
    }


    private void showTitleAll(Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showTitle(Title.title(
                    title,
                    subtitle,
                    Title.Times.times(
                            Duration.ofMillis(fadeIn*50L), Duration.ofMillis(stay*50L), Duration.ofMillis(fadeOut*50L)
                    )
            ));
        }
    }

    private void showScoreBoard() {
        Component scoreboard = Component.text("Счет: ")
                .append(Component.text("КТ " + scoreKT).color(NamedTextColor.BLUE))
                .append(Component.text(" : "))
                .append(Component.text("T " + scoreT).color(NamedTextColor.RED));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(scoreboard);
        }
    }
}