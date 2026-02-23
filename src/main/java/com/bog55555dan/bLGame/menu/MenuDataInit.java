package com.bog55555dan.bLGame.menu;

import com.bog55555dan.bLGame.KEYS.KEYS;
import com.bog55555dan.bLGame.shopItem.ShopItem;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class MenuDataInit {
    private HashMap<ItemStack, Integer> items_price = new HashMap<>();
    private HashMap<ItemStack, Integer> items_slot = new HashMap<>();
    private List<Integer> prices;
    private String title;
    private ShopItem.Type type;
    private String temp;
    private JavaPlugin plugin;

    public MenuDataInit(JavaPlugin plugin, ShopItem.Type type) {
        this.plugin = plugin;
        this.type = type;
        switch (type){
            case KT:
                temp = "ktshop";
                break;
            case T:
                temp = "tshop";
                break;
            case ALL:
                temp = "allshop";
                break;
            case KIT_KT:
                temp = "kitstartKT";
                break;
            case KIT_T:
                temp = "kitstartT";
                break;
            case MAP_CH:
                temp = "maps";
                break;
        }
    }

    public void init() {
        FileConfiguration config = plugin.getConfig();
        items_price.clear();
        items_slot.clear();

        ConfigurationSection ktSection = config.getConfigurationSection(temp);
        if (ktSection == null) {
            plugin.getLogger().severe("Раздел '"+ temp +"' не найден или нечитаем!");
            return;
        }

        title = config.getString(temp + ".title", "Меню");

        if (temp.equals("maps")) {
            ConfigurationSection maps = config.getConfigurationSection(temp);
            if (maps == null) {
                plugin.getLogger().severe("Секция maps не найдена в config.yml!");
                return;
            }
            Set<String> mapIds = maps.getKeys(false);
            for (String mapId : mapIds){
                try {
                    int slot = config.getInt("maps." + mapId + ".slot");
                    int amount = config.getInt("maps." + mapId + ".amount");
                    String display_name = config.getString("maps." + mapId + ".display_name");
                    String materialName = config.getString("maps." + mapId + ".material");
                    Material material = Material.getMaterial(materialName);
                    if (material == null) {
                        plugin.getLogger().severe("Материал '" + materialName + "' не найден!");
                        continue;
                    }

                    if (amount <= 0) amount = 1;
                    ItemStack item = new ItemStack(material, amount);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(display_name);
                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, mapId), PersistentDataType.STRING, UUID.randomUUID().toString());
                    item.setItemMeta(meta);
                    items_slot.put(item, slot);
                    title = "Выбор карты";

                } catch (Exception e) {
                    plugin.getLogger().severe("CRITICAL ERROR при загрузке карты'" + mapId + "': " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return;
        }

        prices = config.getIntegerList(temp + ".prices");

        ConfigurationSection itemsSection = config.getConfigurationSection(temp + ".items");
        if (itemsSection == null) {
            plugin.getLogger().severe("Секция '" + temp + ".items' не найдена в config.yml!");
            return;
        }
        Set<String> itemsIds = itemsSection.getKeys(false);
        for (String itemId : itemsIds){
            try {
                int price = config.getInt(temp + ".items." + itemId + ".price");
                int amount = config.getInt(temp + ".items." + itemId + ".amount");
                int stack = config.getInt(temp + ".items." + itemId + ".stack");
                int slot = config.getInt(temp + ".items." + itemId + ".slot");
                String display_name = config.getString(temp + ".items." + itemId + ".display_name");
                Object maxdamage = config.get(temp + ".items." + itemId + ".maxdamage");

                String materialName = config.getString(temp + ".items." + itemId + ".material");
                Material material = Material.getMaterial(materialName);
                if (material == null) {
                    plugin.getLogger().severe("Материал '" + materialName + "' не найден!");
                    continue;
                }
                if (amount <= 0) amount = 1;
                ItemStack item = new ItemStack(material, amount);
                ItemMeta meta = item.getItemMeta();
                if (meta == null)
                    continue;
                meta.setMaxStackSize(stack);
                if (display_name != null)
                    meta.setDisplayName(display_name);

                ConfigurationSection section = config.getConfigurationSection(temp + ".items." + itemId + ".enchantments");
                if (section != null) {
                    Set<String> enchIds = section.getKeys(false);
                    for (String enchId : enchIds) {
                        try {
                            int level = config.getInt(temp +".items." + itemId + ".enchantments." + enchId);
                            meta.addEnchant(Objects.requireNonNull(Enchantment.getByName(enchId)), level, true);
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

                if (material.toString().contains("POTION") || material.equals(Material.TIPPED_ARROW)) {
                    try {
                        List<?> effectList = config.getList(temp + ".items." + itemId + ".effects");
                        List<PotionEffect> effects = new ArrayList<>();
                        if (effectList != null) {
                            for (Object obj : effectList) {
                                if (obj instanceof Map) {
                                    Map<?, ?> map = (Map<?, ?>) obj;
                                    PotionEffectType type = PotionEffectType.getByName((String) map.get("type"));
                                    int amplifier = (int) map.get("amplifier");
                                    int duration = (int) map.get("duration");

                                    if (type != null) {
                                        effects.add(new PotionEffect(type, duration, amplifier));
                                    }
                                }
                            }
                        }
                        if (meta instanceof PotionMeta potionMeta) {
                            for (PotionEffect effect : effects) {
                                potionMeta.addCustomEffect(effect, true);
                            }
                            Color color = hexToColor(config.getString(temp + ".items." + itemId + ".color"));
                            potionMeta.setColor(color);
                            item.setItemMeta(potionMeta);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().severe("CRITICAL ERROR при загрузке зелья или стрелы'" + itemId + "': " + e.getClass().getSimpleName() + " - " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                if (material.equals(Material.FIREWORK_ROCKET)){
                    try {
                        if (meta instanceof FireworkMeta fireworkMeta) {
                            int power = config.getInt(temp + ".items." + itemId + ".power");
                            FireworkEffect.Type type = FireworkEffect.Type.valueOf(config.getString(temp + ".items." + itemId + ".type"));
                            boolean flicker = plugin.getConfig().getBoolean(temp + ".items." + itemId + ".flicker");
                            boolean trail = plugin.getConfig().getBoolean(temp + ".items." + itemId + ".trail");
                            Color color = hexToColor(config.getString(temp + ".items." + itemId + ".color"));
                            Color fade = hexToColor(config.getString(temp + ".items." + itemId + ".colorFade"));

                            fireworkMeta.setPower(power);
                            FireworkEffect effect = FireworkEffect.builder()
                                    .with(type)
                                    .flicker(flicker)
                                    .trail(trail)
                                    .withColor(color)
                                    .withFade(fade)
                                    .build();
                            fireworkMeta.addEffect(effect);
                            item.setItemMeta(fireworkMeta);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().severe("CRITICAL ERROR при загрузке фейерверка'" + itemId + "': " + e.getClass().getSimpleName() + " - " + e.getMessage());
                        e.printStackTrace();
                    }
                }


                items_price.put(item, price);
                items_slot.put(item, slot);
            }
            catch (Exception e) {
                plugin.getLogger().severe("CRITICAL ERROR при загрузке предмета '" + itemId + "': " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void saveToConfig(Player player, Inventory inv) {
        FileConfiguration config = plugin.getConfig();
        config.set(temp + ".items", null);

        for (int i = 0; i < inv.getSize(); i++) {
            try {
                ItemStack item = inv.getItem(i);
                if (item == null || item.getType() == Material.GOLD_INGOT || item.getPersistentDataContainer().has(KEYS.SET_KEY)) {
                    continue;
                }

                String itemId = "id" + UUID.randomUUID();
                ItemMeta meta = item.getItemMeta();

                String display = meta.getDisplayName();
                config.set(temp + ".items." + itemId + ".display_name", display);

                String material = item.getType().toString();
                config.set(temp + ".items." + itemId + ".material", material);

                if (prices != null || !prices.isEmpty()) {
                    int price = prices.get(i / 9);
                    config.set(temp + ".items." + itemId + ".price", price);
                }

                int amount = item.getAmount();
                config.set(temp + ".items." + itemId + ".amount", amount);

                int stack = item.getMaxStackSize();
                config.set(temp + ".items." + itemId + ".stack", stack);

                if (meta instanceof Damageable damageable && damageable.hasMaxDamage()) {
                    int maxdamage = damageable.getMaxDamage();
                    config.set(temp + ".items." + itemId + ".maxdamage", maxdamage);
                } else {
                    config.set(temp + ".items." + itemId + ".maxdamage", null);
                }

                if (meta instanceof PotionMeta potionMeta && potionMeta.hasColor()) {
                    Color color = potionMeta.getColor();
                    config.set(temp + ".items." + itemId + ".color", String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
                }

                if (meta.hasEnchants()) {
                    for (Map.Entry<Enchantment, Integer> ench : meta.getEnchants().entrySet()){
                        config.set(temp + ".items." + itemId + ".enchantments." + ench.getKey().getKey().getKey(), ench.getValue());
                    }
                }

                if (item.getType().toString().contains("POTION") ||
                        item.getType() == Material.TIPPED_ARROW) {
                    if (meta instanceof PotionMeta potionMeta) {
                        List<Map<String, Object>> effectsList = new ArrayList<>();

                        PotionType type = potionMeta.getBasePotionType();
                        if (type != null) {
                            List<PotionEffect> baseEffects = type.getPotionEffects();
                            for (PotionEffect effect : baseEffects) {
                                Map<String, Object> effectMap = new HashMap<>();
                                effectMap.put("duration", effect.getDuration());
                                effectMap.put("amplifier", effect.getAmplifier());
                                effectMap.put("type", effect.getType().getName());
                                effectsList.add(effectMap);
                            }
                        }
                        for (PotionEffect effect : potionMeta.getCustomEffects()) {
                            Map<String, Object> effectMap = new HashMap<>();
                            effectMap.put("duration", effect.getDuration());
                            effectMap.put("amplifier", effect.getAmplifier());
                            effectMap.put("type", effect.getType().getName());
                            effectsList.add(effectMap);
                        }

                        config.set(temp + ".items." + itemId + ".effects", effectsList);
                    }
                }
            }
            catch (Exception e) {
                plugin.getLogger().severe("CRITICAL ERROR при загрузке автоконфига '" + "': " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
        plugin.saveConfig();
        player.sendMessage("§aКонфиг сохранён! Не забудьте перезагрузить конфиг!");
    }

    public static int getFreeSlotsInMainInventory(Player player) {
        Inventory inventory = player.getInventory();
        int freeSlots = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType().isAir()) {
                freeSlots++;
            }
        }

        return freeSlots;
    }

    public static Color hexToColor(String hex) {
        hex = hex.replace("#", "");
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return Color.fromRGB(r, g, b);
    }

    public String getTitle(){
        return title;
    }

    public List<Integer> getPrices(){
        return prices;
    }

    public HashMap<ItemStack, Integer> getItems_price() {
        return items_price;
    }

    public HashMap<ItemStack, Integer> getItems_slot() {
        return items_slot;
    }
}
