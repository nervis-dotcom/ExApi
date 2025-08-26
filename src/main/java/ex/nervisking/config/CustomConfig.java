package ex.nervisking.config;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Coordinate;
import ex.nervisking.ModelManager.Pattern.KeyAlphaNum;
import ex.nervisking.utils.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @since 1.0.0
*/
public class CustomConfig {

    private final JavaPlugin plugin;
    private final String fileName;
    private final String folderName;
    private final boolean newFile;
    private FileConfiguration fileConfiguration;
    private File file;

    public CustomConfig(@KeyAlphaNum String fileName, @KeyAlphaNum String folderName, boolean newFile) {
        this.plugin = ExApi.getPlugin();
        this.fileName = fileName.endsWith(".yml") ? fileName : fileName + ".yml";
        this.folderName = folderName;
        this.newFile = newFile;
        this.fileConfiguration = null;
        this.file = null;
        this.registerConfig();
    }

    /**
     * @since 1.0.2
     */
    public CustomConfig(@KeyAlphaNum String fileName, boolean newFile) {
        this(fileName, null, newFile);
    }

    /**
     * @since 1.0.2
     */
    public CustomConfig(@KeyAlphaNum String fileName) {
        this(fileName, null, false);
    }

    /**
     * @since 1.0.2
     */
    public CustomConfig(@KeyAlphaNum String fileName, @KeyAlphaNum String folderName) {
        this(fileName, folderName, false);
    }

    /**
     * @since 1.0.2
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull CustomConfig of(@KeyAlphaNum String fileName, @KeyAlphaNum String folderName, boolean newFile) {
        return new CustomConfig(fileName, folderName, newFile);
    }

    /**
     * @since 1.0.2
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull CustomConfig of(@KeyAlphaNum String fileName, @KeyAlphaNum String folderName) {
        return new CustomConfig(fileName, folderName);
    }

    /**
     * @since 1.0.2
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull CustomConfig of(@KeyAlphaNum String fileName, boolean newFile) {
        return new CustomConfig(fileName, newFile);
    }

    /**
     * @since 1.0.2
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull CustomConfig of(@KeyAlphaNum String fileName) {
        return new CustomConfig(fileName);
    }

    public String getPath() {
        return this.fileName;
    }

    private void registerConfig() {
        if (folderName != null) {
            file = new File(plugin.getDataFolder() + File.separator + folderName, fileName);
        } else {
            file = new File(plugin.getDataFolder(), fileName);
        }

        if (!file.exists()) {
            if (newFile) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (folderName != null) {
                    plugin.saveResource(folderName + File.separator + fileName, false);
                } else {
                    plugin.saveResource(fileName, false);
                }
            }

        }

        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            reloadConfig();
        }

        return fileConfiguration;
    }

    public void reloadConfig() {
        if (fileConfiguration == null) {
            if (folderName != null) {
                file = new File(plugin.getDataFolder() + File.separator + folderName, fileName);
            } else {
                file = new File(plugin.getDataFolder(), fileName);
            }

        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);

        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    /**
     * @since 1.0.1
     */
    public ItemBuilder createItem(String path) {
        FileConfiguration config = getConfig();
        String id = config.getString(path + ".id", "barrier");
        String name = config.getString(path + ".name", "");
        int amount = config.getInt(path + ".amount", 1);
        List<String> lore = config.getStringList(path + ".lore");

        // Encantamientos
        Map<String, Integer> enchantments = new HashMap<>();
        if (config.isConfigurationSection(path + ".enchantments")) {
            ConfigurationSection enchantSection = config.getConfigurationSection(path + ".enchantments");
            if (enchantSection != null) {
                for (String key : enchantSection.getKeys(false)) {
                    enchantments.put(key, enchantSection.getInt(key));
                }
            }
        }

        int durability = config.getInt(path + ".durability", -1);
        int maxDamage = config.getInt(path + ".max-durability", -1);
        List<String> flags = config.getStringList(path + ".flags");
        boolean glow = config.getBoolean(path + ".glow", false);
        boolean unbreakable = config.getBoolean(path + ".unbreakable", false);
        int modelData = config.getInt(path + ".custom-model-data", -1);

        // Color
        Color color = null;
        String[] nameColor = config.getString(path + ".color", "").split(";");
        if (nameColor.length >= 3) {
            try {
                int r = Integer.parseInt(nameColor[0]);
                int g = Integer.parseInt(nameColor[1]);
                int b = Integer.parseInt(nameColor[2]);
                color = Color.fromRGB(r, g, b);
            } catch (NumberFormatException ignored) {}
        }

        // Crear item
        ItemBuilder item = ItemBuilder.of(id)
                .setName(name)
                .setLore(lore)
                .setAmount(amount)
                .addEnchant(enchantments)
                .setGlintOverride(glow)
                .setUnbreakable(unbreakable)
                .addItemFlagsByName(flags)
                .setCustomModelData(modelData);

        if (maxDamage != -1) item.setMaxDamage(maxDamage);
        if (durability != -1) item.setDamage(durability);
        if (color != null) item.setColor(color);

        // Otros componentes
        item.setHideTooltip(config.getBoolean(path + ".hide-tooltip", false));

        String rarity = config.getString(path + ".rarity");
        if (rarity != null) {
            item.setRarity(rarity);
        }


        if (config.isConfigurationSection(path + ".item-model")) {
            ConfigurationSection model = config.getConfigurationSection(path + ".item-model");
            if (model != null) {
                item.setItemModel(model.getString("namespace"), model.getString("key"));
            }
        }

        int maxStack = config.getInt(path + ".max-stack", -1);
        if (maxStack != -1) {
            item.setMaxStackSize(maxStack);
        }

        // FoodComponent
        if (config.isConfigurationSection(path + ".food-component")) {
            ConfigurationSection food = config.getConfigurationSection(path + ".food-component");
            if (food != null) {
                item.setFoodComponent(food.getInt("hunger"), (float) food.getDouble("saturation"), food.getBoolean("can-always-eat", false));
            }
        }

        // ArmorTrim
        if (config.isConfigurationSection(path + ".armor-trim")) {
            String pattern = config.getString(path + ".armor-trim.pattern");
            String material = config.getString(path + ".armor-trim.material");
            item.setTrimByName(pattern, material);
        }

        return item.clearError();
    }

    public CustomItem fromConfig(String path) {
        FileConfiguration config = getConfig();
        CustomItem item = new CustomItem();

        item.setId(config.getString(path + ".id", "barrier"));
        item.setName(config.getString(path + ".name", ""));
        item.setAmount(config.getInt(path + ".amount", 1));
        item.setLore(config.getStringList(path + ".lore"));
        item.setFlags(config.getStringList(path + ".flags"));
        item.setGlow(config.getBoolean(path + ".glow", false));
        item.setUnbreakable(config.getBoolean(path + ".unbreakable", false));
        item.setModelData(config.getInt(path + ".custom-model-data", -1));
        item.setDurability(config.getInt(path + ".durability", -1));
        item.setMaxDurability(config.getInt(path + ".max-durability", -1));
        item.setHideTooltip(config.getBoolean(path + ".hide-tooltip", false));
        item.setRarity(config.getString(path + ".rarity"));
        item.setMaxStack(config.getInt(path + ".max-stack", -1));

        // Encantamientos
        if (config.isConfigurationSection(path + ".enchantments")) {
            ConfigurationSection enchantSection = config.getConfigurationSection(path + ".enchantments");
            if (enchantSection != null) {
                Map<String, Integer> enchantments = new HashMap<>();
                for (String key : enchantSection.getKeys(false)) {
                    enchantments.put(key, enchantSection.getInt(key));
                }
                item.setEnchantments(enchantments);
            }
        }

        // Color
        String[] colorSplit = config.getString(path + ".color", "").split(";");
        if (colorSplit.length >= 3) {
            try {
                int r = Integer.parseInt(colorSplit[0]);
                int g = Integer.parseInt(colorSplit[1]);
                int b = Integer.parseInt(colorSplit[2]);
                item.setColor(Color.fromRGB(r, g, b));
            } catch (NumberFormatException ignored) {}
        }

        // Modelo personalizado
        if (config.isConfigurationSection(path + ".item-model")) {
            ConfigurationSection model = config.getConfigurationSection(path + ".item-model");
            if (model != null) {
                item.setModelNamespace(model.getString("namespace"));
                item.setModelKey(model.getString("key"));
            }
        }

        // Food component
        if (config.isConfigurationSection(path + ".food-component")) {
            ConfigurationSection food = config.getConfigurationSection(path + ".food-component");
            if (food != null) {
                item.setFoodHunger(food.getInt("hunger"));
                item.setFoodSaturation((float) food.getDouble("saturation"));
                item.setCanAlwaysEat(food.getBoolean("can-always-eat", false));
            }
        }

        // Armor Trim
        if (config.isConfigurationSection(path + ".armor-trim")) {
            item.setTrimPattern(config.getString(path + ".armor-trim.pattern"));
            item.setTrimMaterial(config.getString(path + ".armor-trim.material"));
        }

        return item;
    }

    public @NotNull Coordinate getCoordinate(String path) {
        FileConfiguration config = getConfig();

        String world = config.getString(path + "world");

        double x, y, z;
        float yaw, pith;
        try {
            x = config.getDouble(path + "x");
            y = config.getDouble(path + "y");
            z = config.getDouble(path + "z");
            yaw = (float) config.getDouble(path + "yaw");
            pith = (float) config.getDouble(path + "pith");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Coordinate.of(world, x, y, z, yaw, pith);
    }
}