package ex.nervisking.config;

import ex.nervisking.ExApi;
import ex.nervisking.utils.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

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
    private FileConfiguration fileConfiguration = null;
    private File file = null;
    private final String folderName;
    private final boolean newFile;

    public CustomConfig(String fileName, String folderName, boolean newFile) {
        this.fileName = fileName;
        this.folderName = folderName;
        this.plugin = ExApi.getPlugin();
        this.newFile = newFile;
    }

    public String getPath() {
        return this.fileName;
    }

    public void registerConfig() {
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
     * @since 1.1.0
     */
    public ItemBuilder createItem(String path) {
        FileConfiguration config = getConfig();
        String id = config.getString(path + ".id", "ASB");
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
//        // Atributos
//        List<Attribute> attributes = new ArrayList<>();
//        if (config.isConfigurationSection(path + ".attributes")) {
//            ConfigurationSection attrSection = config.getConfigurationSection(path + ".attributes");
//            if (attrSection != null) {
//                for (String attr : attrSection.getKeys(false)) {
//                    String base = path + ".attributes." + attr;
//                    double amount = config.getDouble(base + ".amount");
//                    String operation = config.getString(base + ".operation", "ADD_NUMBER");
//                    String slot = config.getString(base + ".slot", "HAND");
//                    attributes.add(new Attribute(attr, amount, operation, slot));
//                }
//            }
//        }
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
        ItemBuilder item = new ItemBuilder(id)
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

        //for (AttributeData attr : attributes) item.addAttribute(attr);

        // Otros componentes
        item.setHideTooltip(config.getBoolean(path + ".hide-tooltip", false));
        item.setRarity(config.getString(path + ".rarity", "COMMON"));


        if (config.isConfigurationSection(path + ".item-model")) {
            ConfigurationSection model = config.getConfigurationSection(path + ".item-model");
            if (model != null) {
                item.setItemModel(model.getString("namespace"), model.getString("key"));
            }
        }
        //item.setBookEnchant(config.getBoolean(path + ".BookEnchant", false));
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

//        // PotionEffects
//        if (config.isList(path + ".PotionEffects")) {
//            List<String> effects = config.getStringList(path + ".PotionEffects");
//            for (String line : effects) {
//                String[] parts = line.split(";");
//                if (parts.length >= 2) {
//                    String type = parts[0];
//                    int duration = Integer.parseInt(parts[1]);
//                    int amplifier = parts.length >= 3 ? Integer.parseInt(parts[2]) : 0;
//                    item.setPotionEffects(type, duration, amplifier);
//                }
//            }
//        }
//
//        // ComponentFlags / ComponentStrings / ComponentFloats / ComponentColors
//        item.setComponentFlags(config.getStringList(path + ".ComponentFlags"));
//        item.setComponentStrings(config.getConfigurationSection(path + ".ComponentStrings"));
//        item.setComponentFloats(config.getConfigurationSection(path + ".ComponentFloats"));
//        item.setComponentColors(config.getConfigurationSection(path + ".ComponentColors"));
        return item;
    }

}