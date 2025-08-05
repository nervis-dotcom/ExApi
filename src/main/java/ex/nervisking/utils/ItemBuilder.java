package ex.nervisking.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Logger;
import ex.nervisking.itemsManager.ItemUtils;
import ex.nervisking.itemsManager.RDMaterial;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for easily creating custom items.
 * Inspired by builder patterns for clean and readable item creation.
 * Author: Thomas Tran
 * Restore: nervisking
 */
@SuppressWarnings({"removal", "UnstableApiUsage", "deprecation"})
public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;
    private final PersistentDataContainer container;

    private Player player = null;
    private static final Error error = new Error();
    private static final ServerVersion serverVersion = ExApi.serverVersion;

    public final static String CLOSE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0NWU1ZmNlMmZjZmZmNzhjZGFjNjVlZDg4MTkxOGM3OWMzOGU4NTVlYmJjMTkyYzk3YzU3ODRjMzJkMzc4In19fQ==";
    public final static String BACK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ3M2NmNjZkMzFiODNjZDhiODY0NGMxNTk1OGMxYjczYzhkOTczMjNiODAxMTcwYzFkODg2NGJiNmE4NDZkIn19fQ==";
    public final static String AFTER = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ==";
    public final static String READY = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzI5NmQzZTE0OTNmYTMyZDgyN2EzNjM1YTY4M2U1YmRlZDY0OTE0ZDc1ZTczYWFjZGNjYmE0NmQ4ZmQ5MCJ9fX0=";
    public final static String PENDING = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWIyY2RmNDgxNzQ3ZjExOTkzNjQ3OTM3ZTljNDg5ZDhiZWJjYWY1ZjJkODg2ZmQ5OGMyMDMyODQ0ZDEifX19";
    public final static String NOT_READY = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2NjNDcwYWUyNjMxZWZkZmFmOTY3YjM2OTQxM2JjMjQ1MWNkN2EzOTQ2NWRhNzgzNmE2YzdhMTRlODc3In19fQ==";
    public final static String STATE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmY2Nzg5MTU5MWE4OGI5ZDM5OTUwNDdiZjk0MDNkZjI0ZDYwOWNkNjk2YmJmOTdjZDMxZjc4N2ViNGIifX19";
    public final static String OFFLINE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTBkZjU2NDM3MDA1Nzk1Njc5NDk5NTQ4NDk2ODM4ZGJhY2NlYWY3MDVjZmNhZDk5NGFiMjk0YmZmODRlMjk5ZiJ9fX0=";
    public final static String GRAY = "GRAY_STAINED_GLASS_PANE";
    public final static String BLACK = "BLACK_STAINED_GLASS_PANE";
    public final static String WHITE = "WHITE_STAINED_GLASS_PANE";
    public final static String GREEN = "GREEN_STAINED_GLASS_PANE";
    public final static String YELLOW = "YELLOW_STAINED_GLASS_PANE";
    public final static String RED = "RED_STAINED_GLASS_PANE";
    public final static String BARRIER = "BARRIER";

    // ======= Constructors =======

    public ItemBuilder(ItemStack item) {
        if (item.getItemMeta() == null)
            throw new IllegalArgumentException("ItemMeta cannot be null.");

        this.item = item;
        this.meta = item.getItemMeta();
        this.container = meta.getPersistentDataContainer();
    }

    public ItemBuilder(Player player, ItemStack item) {
        if (item.getItemMeta() == null)
            throw new IllegalArgumentException("ItemMeta cannot be null.");

        this.player = player;
        this.item = item;
        this.meta = item.getItemMeta();
        this.container = meta.getPersistentDataContainer();
    }

    public ItemBuilder(Material material, int count) {
        this(new ItemStack(material, count));
    }

    public ItemBuilder(Player player, Material material, int count) {
        this(player, new ItemStack(material, count));
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Player player, Material material) {
        this(player, material, 1);
    }

    public ItemBuilder(Player player, RDMaterial rdMaterial) {
        this(player, rdMaterial.getRandom(), 1);
    }

    public ItemBuilder(RDMaterial rdMaterial) {
        this(rdMaterial.getRandom(), 1);
    }

    public ItemBuilder(String value, int count) {
        if (value == null) {
            error.setCause("Material incorrecto");
            this.item = createErrorItem();
            this.meta = item.getItemMeta();
            this.container = meta.getPersistentDataContainer();
            return;
        }

        Material material = getMaterial(value);
        if (material != Material.AIR) {
            this.item = new ItemStack(material, count);
        } else if (value.startsWith("ey") || value.startsWith("[texture]")) {
            this.item = texture(value.startsWith("[texture]") ? value.substring("[texture]".length()).trim() : value, count);
        } else if (value.startsWith("[user]")) {
            this.item = createSkullFromUsername(value.substring("[user]".length()).trim());
        } else if (value.toLowerCase().startsWith("[random]")) {
            RDMaterial type = RDMaterial.fromString(value.substring("[random]".length()).trim());
            if (type != null) {
                Material mat = type.getRandom();
                this.item = new ItemStack(mat, count);
            } else {
                this.item = createErrorItem();
            }
        } else {
            this.item = createSkullFromUsername(value);
        }

        if (item.getItemMeta() == null)
            throw new IllegalArgumentException("ItemMeta cannot be null.");

        this.meta = item.getItemMeta();
        this.container = meta.getPersistentDataContainer();
    }

    public ItemBuilder(String value) {
        this(value, 1);
    }

    public ItemBuilder(Player player, String value) {
        if (value == null || value.isEmpty()) {
            error.setCause("Material incorrecto: " + value);
            this.item = createErrorItem();
            this.meta = item.getItemMeta();
            this.container = meta.getPersistentDataContainer();
            return;
        }

        Material material = getMaterial(value);
        if (material != Material.AIR) {
            this.item = new ItemStack(material, 1);
        } else if (value.startsWith("ey") || value.startsWith("[texture]")) {
            this.item = texture(value.startsWith("[texture]") ? value.substring("[texture]".length()).trim() : value, 1);
        } else if (value.startsWith("[user]")) {
            this.item = createSkullFromUsername(value.substring("[user]".length()).trim());
        } else if (value.toLowerCase().startsWith("[random]")) {
            String trim = value.substring("[random]".length()).trim();
            RDMaterial type = RDMaterial.fromString(trim);
            if (type != null) {
                Material mat = type.getRandom();
                this.item = new ItemStack(mat, 1);
            } else {
                error.setCause("RDMaterial incorrecto: " + trim);
                this.item = createErrorItem();
            }
        } else {
            this.item = createSkullFromUsername(value);
        }
        if (item.getItemMeta() == null)
            throw new IllegalArgumentException("ItemMeta cannot be null.");

        this.player = player;
        this.meta = item.getItemMeta();
        this.container = meta.getPersistentDataContainer();
    }

    public ItemBuilder(UUID uuid) {
        this(createSkullFromUUID(uuid));
    }

    public ItemBuilder(Player player, UUID uuid) {
        this(player, createSkullFromUUID(uuid));
    }

    // ======= Static Helpers =======

    public static ItemBuilder of(Player player, Material material, int count) {
        return new ItemBuilder(player, material, count);
    }

    public static ItemBuilder of(Material material, int count) {
        return new ItemBuilder(material, count);
    }

    public static ItemBuilder of(String material, int count) {
        return new ItemBuilder(material, count);
    }

    public static ItemBuilder of(Player player, Material material) {
        return new ItemBuilder(player, material);
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder of(ItemStack ItemStack) {
        return new ItemBuilder(ItemStack);
    }

    public static ItemBuilder of(Player player, RDMaterial rdMaterial) {
        return new ItemBuilder(player, rdMaterial);
    }

    public static ItemBuilder of(RDMaterial rdMaterial) {
        return new ItemBuilder(rdMaterial);
    }

    public static ItemBuilder of(String material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder of(Player player, String material) {
        return new ItemBuilder(player, material);
    }

    public static ItemBuilder of(UUID uuid) {
        return new ItemBuilder(uuid);
    }

    public static ItemBuilder of(Player player, UUID uuid) {
        return new ItemBuilder(player, uuid);
    }

    private static ItemStack createErrorItem() {
        ItemStack errorItem = new ItemStack(Material.BARRIER);
        ItemMeta meta = errorItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ExApi.getUtils().setColoredMessage(error.getCause() != null ? error.getCause() : "&cERROR"));
            errorItem.setItemMeta(meta);
        }
        return errorItem;
    }

    private static @NotNull Material getMaterial(String name) {
        if (name == null || name.isEmpty()) return Material.AIR;
        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.AIR;
        }
    }

    private static ItemStack createSkullFromUsername(String user) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (head.getItemMeta() instanceof SkullMeta skullMeta) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(user);
            if (offlinePlayer.hasPlayedBefore()) {
                if (offlinePlayer.isOnline()) {
                    skullMeta.setPlayerProfile(offlinePlayer.getPlayerProfile());
                } else {
                    return texture(OFFLINE, 1);
                }
            } else {
                error.setCause("Material incorrecto: " + user);
                return createErrorItem();
            }

            head.setItemMeta(skullMeta);
        }
        return head;
    }

    private static ItemStack createSkullFromUUID(UUID uuid) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (head.getItemMeta() instanceof SkullMeta skullMeta) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.hasPlayedBefore()) {
                if (offlinePlayer.isOnline()) {
                    skullMeta.setPlayerProfile(offlinePlayer.getPlayerProfile());
                } else {
                    return texture(OFFLINE, 1);
                }
            } else {
                return createErrorItem();
            }
            head.setItemMeta(skullMeta);
        }
        return head;
    }

    private static ItemStack texture(String texture, int amount) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        head.setAmount(amount);
        if (head.getItemMeta() instanceof SkullMeta skullMeta && texture != null) {
            if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_20_R2)) {
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();
                URL url;
                try {
                    String decoded = new String(Base64.getDecoder().decode(texture));
                    String decodedFormatted = decoded.replaceAll("\\s", "");
                    JsonObject jsonObject = new Gson().fromJson(decodedFormatted, JsonObject.class);
                    String urlText = jsonObject.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();

                    url = new URL(urlText);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    error.setCause(exception.getMessage());
                    return createErrorItem();
                }
                textures.setSkin(url);
                profile.setTextures(textures);

                skullMeta.setOwnerProfile(profile);
            } else {
                GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                profile.getProperties().put("textures", new Property("textures", texture));

                try {
                    Field profileField = skullMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(skullMeta, profile);
                } catch (IllegalArgumentException | NoSuchFieldException | SecurityException |
                         IllegalAccessException ignored) {
                }
            }

            head.setItemMeta(skullMeta);

        }
        return head;
    }

    // ======= Modifiers =======

    private final Map<String, String> customVariables = new HashMap<>();

    public ItemBuilder setVariable(String key, String value) {
        if (key != null && value != null) {
            customVariables.put(key, value);
        }
        return this;
    }

    public ItemBuilder replaceVariables() {
        if (error.isStatus()) return this;

        // Reemplazar nombre
        if (meta.hasDisplayName()) {
            meta.setDisplayName(parse(meta.getDisplayName()));
        }

        // Reemplazar lore
        if (meta.hasLore()) {
            List<String> newLore = new ArrayList<>();
            for (String line : Objects.requireNonNull(meta.getLore())) {
                newLore.add(parse(line));
            }
            meta.setLore(newLore);
        }

        customVariables.clear();
        return this;
    }

    private String parse(String text) {
        if (text == null || text.isEmpty()) return "";

        // Reemplazo de variables personalizadas
        for (Map.Entry<String, String> entry : customVariables.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        // PlaceholderAPI (si hay player)
        if (player != null) {
           return ExApi.getUtils().setPlaceholders(player, text);
        }

        // Colores
        return ExApi.getUtils().setColoredMessage(text);
    }

    public ItemBuilder setType(Material material) {
        if (error.isStatus() || material == null || material.isEmpty()) {
            return this;
        }
        item.setType(material);
        return this;
    }

    public ItemBuilder setType(String material) {
        if (error.isStatus() || material == null || material.isEmpty()) {
            return this;
        }
        Material mat = getMaterial(material);
        if (mat != Material.AIR) {
            item.setType(mat);
        } else {
            throw new IllegalArgumentException("Invalid material: " + material);
        }
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setName(String name) {
        if (error.isStatus() || name == null || name.isEmpty()) {
            return this;
        }
        meta.setDisplayName(parse(name));
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        if (error.isStatus() || lore == null || lore.length == 0) {
            return this;
        }
        List<String> currentLore = new ArrayList<>();
        for (String line : lore) {
            currentLore.add(parse(line));
        }
        meta.setLore(currentLore);

        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (error.isStatus() || lore == null || lore.isEmpty()) {
            return this;
        }
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(parse(line));
        }
        meta.setLore(coloredLore);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        if (error.isStatus() || lore == null || lore.length == 0) {
            return this;
        }
        List<String> currentLore = meta.hasLore() ? new ArrayList<>(Objects.requireNonNull(meta.getLore())) : new ArrayList<>();
        for (String line : lore) {
            currentLore.add(parse(line));
        }
        meta.setLore(currentLore);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        if (error.isStatus() || lore == null || lore.isEmpty()) {
            return this;
        }
        List<String> currentLore = meta.hasLore() ? new ArrayList<>(Objects.requireNonNull(meta.getLore())) : new ArrayList<>();
        for (String line : lore) {
            currentLore.add(parse(line));
        }
        meta.setLore(currentLore);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        if (error.isStatus() || flags == null || flags.length == 0) {
            return this;
        }
        meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder addItemFlagsByName(List<String> flags) {
        if (error.isStatus() || flags == null || flags.isEmpty()) {
            return this;
        }
        for (String s : flags) {
            meta.addItemFlags(ItemFlag.valueOf(s));
        }
        return this;
    }

    public ItemBuilder addItemFlagsByName(String... flags) {
        if (error.isStatus() || flags == null) {
            return this;
        }
        for (String name : flags) {
            try {
                meta.addItemFlags(ItemFlag.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.out.println("Advertencia: Flag inválido '" + name + "'");
            }
        }
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        if (error.isStatus()) {
            return this;
        }
        meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        if (error.isStatus()) {
            return this;
        }
        meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return this;
    }

    /**
     * @since 1.1.0
     */
    public ItemBuilder addEnchant(Map<String, Integer> enchantments) {
        if (error.isStatus() || enchantments == null || enchantments.isEmpty()) {
            return this;
        }
        for (Map.Entry<String, Integer> enchantment : enchantments.entrySet()) {
            Enchantment enchant = Enchantment.getByName(enchantment.getKey().toUpperCase());
            if (enchant != null) {
                meta.addEnchant(enchant, enchantment.getValue(), true);
            }
        }
        return this;
    }

    public ItemBuilder addEnchantByName(String name, int level, boolean ignoreLevelRestriction) {
        if (error.isStatus()) {
            return this;
        }
        Enchantment enchant = Enchantment.getByName(name.toUpperCase());
        if (enchant != null) {
            return addEnchant(enchant, level, ignoreLevelRestriction);
        }
        throw new IllegalArgumentException("Enchantment '" + name + "' does not exist");
    }

    public ItemBuilder setDamage(int damage) {
        if (error.isStatus()) {
            return this;
        }
        if (meta instanceof Damageable d) {
            d.setDamage(damage);
        }
        return this;
    }

    public ItemBuilder setMaxDamage(int damage) {
        if (error.isStatus()) {
            return this;
        }
        if (meta instanceof Damageable damageable) {
            damageable.setMaxDamage(damage);
        }
        return this;
    }

    public ItemBuilder setRepairCost(int repairCost) {
        if (error.isStatus()) {
            return this;
        }
        if (meta instanceof Repairable repairable) {
            repairable.setRepairCost(repairCost);
        }
        return this;
    }

    public <T, Z> ItemBuilder setPersistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z data) {
        if (error.isStatus()) {
            return this;
        }
        container.set(key, type, data);
        return this;
    }

    public <T, Z> ItemBuilder setPersistentData(String key, PersistentDataType<T, Z> type, Z data) {
        if (error.isStatus()) {
            return this;
        }
        container.set(new NamespacedKey(ExApi.getPlugin(), key), type, data);
        return this;
    }

    @Deprecated(since = "1.0.0", forRemoval = true)
    public <T, Z> ItemBuilder addPersistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z data) {
        if (error.isStatus()) {
            return this;
        }
        container.set(key, type, data);
        return this;
    }

    @Deprecated(since = "1.0.0", forRemoval = true)
    public <T, Z> ItemBuilder addPersistentData(String key, PersistentDataType<T, Z> type, Z data) {
        if (error.isStatus()) {
            return this;
        }
        container.set(new NamespacedKey(ExApi.getPlugin(), key), type, data);
        return this;
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        if (error.isStatus()) {
            return this;
        }
        meta.addAttributeModifier(attribute, modifier);
        return this;
    }

    public ItemBuilder addAttributeModifierByName(String attributeName, AttributeModifier modifier) {
        if (error.isStatus()) {
            return this;
        }
        try {
            Attribute attribute = Attribute.valueOf(attributeName.toUpperCase());
            meta.addAttributeModifier(attribute, modifier);
        } catch (IllegalArgumentException e) {
            ExApi.getUtilsManagers().sendLogger(Logger.WARNING,"Atributo inválido '" + attributeName + "'");
        }
        return this;
    }

    public ItemBuilder setHide() {
        if (error.isStatus()) {
            return this;
        }
        meta.addItemFlags(ItemFlag.values());
        return this;
    }

    public ItemBuilder setHideAll() {
        if (error.isStatus()) {
            return this;
        }
        meta.addItemFlags(ItemFlag.values());
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            for (Attribute attribute : Attribute.values()) {
                meta.removeAttributeModifier(attribute);
            }
        }
        return this;
    }

    public ItemBuilder setHideAll(boolean value) {
        if (error.isStatus()) {
            return this;
        }
        if (value) {
            meta.addItemFlags(ItemFlag.values());
            if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
                for (Attribute attribute : Attribute.values()) {
                    meta.removeAttributeModifier(attribute);
                }
            }
        }
        return this;
    }

    public ItemBuilder setUnbreakable() {
        if (error.isStatus()) {
            return this;
        }
        meta.setUnbreakable(true);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean value) {
        if (error.isStatus()) {
            return this;
        }
        meta.setUnbreakable(value);
        return this;
    }

    public ItemBuilder setSkull(String user) {
        if (error.isStatus()) {
            return this;
        }

        if (meta instanceof SkullMeta skullMeta) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(user);
            if (offlinePlayer.hasPlayedBefore()) {
                if (offlinePlayer.isOnline()) {
                    skullMeta.setPlayerProfile(offlinePlayer.getPlayerProfile());
                } else {
                    return setSkullTexture(OFFLINE);
                }
            }
            item.setItemMeta(skullMeta);
        }
        return this;
    }

    public ItemBuilder setSkull(UUID uuid) {
        if (error.isStatus()) {
            return this;
        }

        if (meta instanceof SkullMeta skullMeta) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.hasPlayedBefore()) {
                if (offlinePlayer.isOnline()) {
                    skullMeta.setPlayerProfile(offlinePlayer.getPlayerProfile());
                } else {
                    return setSkullTexture(OFFLINE);
                }
            }
            item.setItemMeta(skullMeta);
        }
        return this;
    }

    @Deprecated(since = "1.0.0", forRemoval = true)
    public ItemBuilder SkullTexture(String texture) {
        if (error.isStatus()) {
            return this;
        }
        if (meta instanceof SkullMeta skullMeta) {
            if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_20_R2)) {
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();
                URL url;
                try {
                    String decoded = new String(Base64.getDecoder().decode(texture));
                    String decodedFormatted = decoded.replaceAll("\\s", "");
                    JsonObject jsonObject = new Gson().fromJson(decodedFormatted, JsonObject.class);
                    String urlText = jsonObject.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();

                    url = new URL(urlText);
                } catch (Exception error) {
                    error.printStackTrace();
                    return this;
                }
                textures.setSkin(url);
                profile.setTextures(textures);

                skullMeta.setOwnerProfile(profile);
            } else {
                GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                profile.getProperties().put("textures", new Property("textures", texture));

                try {
                    Field profileField = skullMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(skullMeta, profile);
                } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException ignored) {
                }
            }

        }
        return this;
    }

    public ItemBuilder setSkullTexture(String texture) {
        return this.texture(texture, UUID.randomUUID());
    }

    public ItemBuilder setSkullTextureId(String texture) {
        return this.texture(texture, UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    private ItemBuilder texture(String texture, UUID uuid) {
        if (error.isStatus()) {
            return this;
        }
        if (meta instanceof SkullMeta skullMeta) {
            if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_20_R2)) {
                PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
                PlayerTextures textures = profile.getTextures();
                URL url;
                try {
                    String decoded = new String(Base64.getDecoder().decode(texture));
                    String decodedFormatted = decoded.replaceAll("\\s", "");
                    JsonObject jsonObject = new Gson().fromJson(decodedFormatted, JsonObject.class);
                    String urlText = jsonObject.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();

                    url = new URL(urlText);
                } catch (Exception error) {
                    error.printStackTrace();
                    return this;
                }
                textures.setSkin(url);
                profile.setTextures(textures);

                skullMeta.setOwnerProfile(profile);
            } else {
                GameProfile profile = new GameProfile(uuid, "");
                profile.getProperties().put("textures", new Property("textures", texture));

                try {
                    Field profileField = skullMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(skullMeta, profile);
                } catch (IllegalArgumentException | NoSuchFieldException | SecurityException |
                         IllegalAccessException ignored) {
                }
            }

        }
        return this;
    }

    public ItemBuilder setMaxStackSize(int amount) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            meta.setMaxStackSize(amount);
        }
        return this;
    }

    @Deprecated(since = "1.0.0", forRemoval = true)
    public ItemBuilder setEnchantmentGlintOverride() {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            meta.setEnchantmentGlintOverride(true);
        } else {
            addEnchant(Enchantment.FLAME, 1, true);
            addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    @Deprecated(since = "1.0.0", forRemoval = true)
    public ItemBuilder setEnchantmentGlintOverride(boolean value) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            meta.setEnchantmentGlintOverride(value);
        } else if (value) {
            addEnchant(Enchantment.LURE, 1, true);
            addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    /**
     * @since 1.0.1
     */
    public ItemBuilder setGlintOverride(boolean value) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            meta.setEnchantmentGlintOverride(value);
        } else if (value) {
            addEnchant(Enchantment.LURE, 1, true);
            addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    /**
     * @since 1.0.1
     */
    public ItemBuilder setGlintOverride() {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            meta.setEnchantmentGlintOverride(true);
        } else {
            addEnchant(Enchantment.LURE, 1, true);
            addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder setGlider() {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            meta.setGlider(true);
        } else {
            addEnchant(Enchantment.LURE, 1, true);
            addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder setGlider(boolean value) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            meta.setGlider(value);
        } else if (value) {
            addEnchant(Enchantment.LURE, 1, true);
            addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder setHideTooltip() {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            meta.setHideTooltip(true);
        } else {
            setName(" ");
        }
        return this;
    }

    public ItemBuilder setHideTooltip(boolean value) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            meta.setHideTooltip(value);
        } else if (value) {
            setName(" ");
        }
        return this;
    }

    public ItemBuilder setFoodComponent(int nutrition, float saturationModifier, boolean canAlwaysEat) {
        if (error.isStatus()) {
            return this;
        }
        if (meta instanceof FoodComponent foodMeta) {
            if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
                foodMeta.setNutrition(nutrition);
                foodMeta.setSaturation(saturationModifier);
                foodMeta.setCanAlwaysEat(canAlwaysEat);
            }
        } else {
            throw new IllegalArgumentException("El item no admite propiedades de comida (FoodMeta)");
        }
        return this;
    }

    public ItemBuilder setRarity(String rarityName) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            try {
                meta.setRarity(ItemRarity.valueOf(rarityName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                ExApi.getUtilsManagers().sendLogger(Logger.WARNING, "Rarity inválido: " + rarityName);
            }
        }
        return this;
    }

    public ItemBuilder setPotionEffects(PotionEffect... effects) {
        if (error.isStatus()) {
            return this;
        }
        if (meta instanceof PotionMeta potionMeta) {
            for (PotionEffect effect : effects) {
                potionMeta.addCustomEffect(effect, true);
            }
        }
        return this;
    }

    public ItemBuilder setPotionEffect(PotionEffect effect) {
        if (error.isStatus()) {
            return this;
        }
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.addCustomEffect(effect, true);
        }
        return this;
    }

    public ItemBuilder setColor(Color color) {
        if (error.isStatus()) {
            return this;
        }
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(color);
        } else if (meta instanceof LeatherArmorMeta leatherMeta) {
            leatherMeta.setColor(color);
        }
        return this;
    }

    public ItemBuilder clearTrim(boolean value) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_9_R2)) {
            if (meta instanceof ArmorMeta potionMeta) {
                if (value) {
                    potionMeta.setTrim(null);
                }
            }
        }
        return this;
    }

    public ItemBuilder setTrim(TrimMaterial trimMaterial, TrimPattern trimPattern) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_9_R2)) {
            if (meta instanceof ArmorMeta armorMeta) {
                armorMeta.setTrim(new ArmorTrim(trimMaterial, trimPattern));
            }
        }
        return this;
    }

    public ItemBuilder setTrimByName(String trim, String Material) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_9_R2)) {
            if (meta instanceof ArmorMeta armorMeta) {
                TrimMaterial trimMaterial = ItemUtils.getTrimMaterial(Material);
                if (trimMaterial == null) {
                    return this;
                }

                TrimPattern trimPattern = ItemUtils.getTrimPattern(trim);
                if (trimPattern == null) {
                    return this;
                }
                armorMeta.setTrim(new ArmorTrim(trimMaterial, trimPattern));
            }
        }
        return this;
    }


    public ItemBuilder addBookEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        if (error.isStatus()) {
            return this;
        }
        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            storageMeta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
        }
        return this;
    }

    public ItemBuilder addBookEnchantByName(String name, int level, boolean ignoreLevelRestriction) {
        if (error.isStatus()) {
            return this;
        }
        Enchantment enchant = Enchantment.getByName(name.toUpperCase());
        if (enchant != null) {
            return addBookEnchant(enchant, level, ignoreLevelRestriction);
        }
        throw new IllegalArgumentException("Enchantment '" + name + "' does not exist");

    }

    public ItemBuilder setComponentFlags(List<Boolean> booleans) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setFlags(booleans);
        }
        return this;
    }

    public ItemBuilder setComponentFlags(Boolean... booleans) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setFlags(List.of(booleans));
        }
        return this;
    }

    public ItemBuilder setComponentFloats(List<Float> floats) {
        if (error.isStatus()) {
            return this;
        }
        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setFloats(floats);
        }
        return this;
    }

    public ItemBuilder setComponentFloats(Float... floats) {
        if (error.isStatus()) {
            return this;
        }

        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setFloats(List.of(floats));
        }
        return this;
    }

    public ItemBuilder setComponentColors(List<Color> colors) {
        if (error.isStatus()) {
            return this;
        }

        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setColors(colors);
        }
        return this;
    }

    public ItemBuilder setComponentColors(Color... colors) {
        if (error.isStatus()) {
            return this;
        }

        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setColors(List.of(colors));
        }
        return this;
    }

    public ItemBuilder setComponentColorsByName(List<String> colors) {
        if (error.isStatus()) {
            return this;
        }

        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setColors(colors.stream()
                    .map(rgb -> Color.fromRGB(Integer.parseInt(rgb)))
                    .collect(Collectors.toList()));
        }
        return this;
    }

    public ItemBuilder setComponentColorsByName(String... colors) {
        if (error.isStatus()) {
            return this;
        }

        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setColors(Stream.of(colors)
                    .map(rgb -> Color.fromRGB(Integer.parseInt(rgb)))
                    .collect(Collectors.toList()));
        }
        return this;
    }

    public ItemBuilder setComponentStrings(List<String> strings) {
        if (error.isStatus()) {
            return this;
        }

        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setStrings(strings);
        }
        return this;
    }

    public ItemBuilder setComponentStrings(String... strings) {
        if (error.isStatus()) {
            return this;
        }

        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setStrings(List.of(strings));
        }
        return this;
    }

    public ItemBuilder setItemModel(String namespace, String key) {
        if (error.isStatus()) {
            return this;
        }

        if (serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_21_R3)) {
            meta.setItemModel(new NamespacedKey(namespace, key));
        }

        return this;
    }

    /**
     * @since 1.0.1
     */
    public ItemBuilder setPlayer(Player player) {
        if (error.isStatus()) {
            return this;
        }
        this.player = player;
        return this;
    }

    // ======= Finalizer =======
    public boolean isError() {
        boolean status = error.isStatus();
        if (error.isStatus()) {
            error.setCause(null); // Reset error state after checking
        }
        return status;
    }

    /**
     * @since 1.0.2
     */
    public ItemBuilder clearError() {
        if (error.isStatus()) {
            error.setCause(null); // Reset error state after checking
        }
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        error.setCause(null); // Reset error state after building
        return item;
    }

    /**
     * @since 1.0.1
     */
    public boolean give() {
        if (error.isStatus()) {
            ExApi.getUtilsManagers().sendLogger(Logger.WARNING, "no se pudo construir el item.");
            return false;
        }
        if (player == null) {
            ExApi.getUtilsManagers().sendLogger(Logger.WARNING, "player es null.");
            return false;
        }
        if (!player.isOnline()) {
            ExApi.getUtilsManagers().sendLogger(Logger.WARNING, "player no online.");
            return false;
        }

        Map<Integer, ItemStack> stackHashMap = player.getInventory().addItem(this.build());

        if (!stackHashMap.isEmpty()) {
            for (ItemStack leftover : stackHashMap.values()) {
                player.getWorld().dropItem(player.getLocation(), leftover);
            }
        }

        return true;
    }

    /**
     * @since 1.0.1
     */
    public void give(String message) {
        if (give()) {
            ExApi.getUtilsManagers().sendMessage(player, message);
        }
    }

    /**
     * @since 1.0.1
     */
    public void drop(Location location) {
        location.getWorld().dropItem(location, this.build());
    }

    /**
     * @since 1.0.1
     */
    public void drop() {
        if (player != null) {
            drop(player.getLocation());
        }
    }

    /**
     * @since 1.0.1
     */
    private static class Error {

        private String cause;

        public Error() {
            this.cause = null;
        }

        public boolean isStatus() {
            return cause != null;
        }

        public String getCause() {
            return cause;
        }

        public void setCause(String cause) {
            this.cause = cause;
        }
    }
}