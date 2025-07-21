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
import java.util.ArrayList;
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

//        if(config.contains(path+".custom_model_component_data")) {
//            List<String> cFlags = new ArrayList<>();
//            List<String> cFloats = new ArrayList<>();
//            List<String> cColors = new ArrayList<>();
//            List<String> cStrings = new ArrayList<>();
//
//            if(config.contains(path+".custom_model_component_data.flags")) {
//                cFlags = config.getStringList(path+".custom_model_component_data.flags");
//            }
//            if(config.contains(path+".custom_model_component_data.floats")) {
//                cFloats = config.getStringList(path+".custom_model_component_data.floats");
//            }
//            if(config.contains(path+".custom_model_component_data.colors")) {
//                cColors = config.getStringList(path+".custom_model_component_data.colors");
//            }
//            if(config.contains(path+".custom_model_component_data.strings")) {
//                cStrings = config.getStringList(path+".custom_model_component_data.strings");
//            }
//
//            item.setComponentFloats(cFloats).setComponentColorsByName(cColors).setComponentStrings(cStrings);
//        }

        return item;
    }

}