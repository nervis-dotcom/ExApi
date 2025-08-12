package ex.nervisking.config;

import ex.nervisking.utils.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItem {

    private String id;
    public String name;
    public List<String> lore;
    private int amount;
    private Map<String, Integer> enchantments;
    private int durability;
    private int maxDurability;
    private List<String> flags;
    private boolean glow;
    private boolean unbreakable;
    private int modelData;
    private Color color;
    private boolean hideTooltip;
    private String rarity;
    private String modelNamespace;
    private String modelKey;
    private int maxStack;
    private int foodHunger;
    private float foodSaturation;
    private boolean canAlwaysEat;
    private String trimPattern;
    private String trimMaterial;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setEnchantments(Map<String, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void setMaxDurability(int maxDurability) {
        this.maxDurability = maxDurability;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public void setModelData(int modelData) {
        this.modelData = modelData;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setHideTooltip(boolean hideTooltip) {
        this.hideTooltip = hideTooltip;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setModelNamespace(String modelNamespace) {
        this.modelNamespace = modelNamespace;
    }

    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }

    public void setFoodHunger(int foodHunger) {
        this.foodHunger = foodHunger;
    }

    public void setFoodSaturation(float foodSaturation) {
        this.foodSaturation = foodSaturation;
    }

    public void setCanAlwaysEat(boolean canAlwaysEat) {
        this.canAlwaysEat = canAlwaysEat;
    }

    public void setTrimPattern(String trimPattern) {
        this.trimPattern = trimPattern;
    }

    public void setTrimMaterial(String trimMaterial) {
        this.trimMaterial = trimMaterial;
    }

    public ItemBuilder getItemBuilder() {
        return toItemBuilder(null, new HashMap<>());
    }

    public ItemBuilder getItemBuilder(Player player) {
        return toItemBuilder(player, new HashMap<>());
    }

    public ItemBuilder getItemBuilder(Map<String, String> variables) {
        return toItemBuilder(null, variables);
    }

    public ItemBuilder toItemBuilder(Player player, Map<String, String> variables) {
        String parsedName = replaceVariables(this.name, variables);

        List<String> parsedLore = this.lore != null
                ? this.lore.stream().map(line -> replaceVariables(line, variables)).toList()
                : List.of();

        ItemBuilder item = (player != null ? ItemBuilder.of(player, this.id) : ItemBuilder.of(this.id))
                .setName(parsedName)
                .setLore(parsedLore)
                .setAmount(this.amount)
                .addEnchant(this.enchantments != null ? this.enchantments : Map.of())
                .setGlintOverride(this.glow)
                .setUnbreakable(this.unbreakable)
                .addItemFlagsByName(this.flags != null ? this.flags : List.of())
                .setCustomModelData(this.modelData)
                .setHideTooltip(this.hideTooltip);

        if (this.maxDurability != -1) item.setMaxDamage(this.maxDurability);
        if (this.durability != -1) item.setDamage(this.durability);
        if (this.color != null) item.setColor(this.color);
        if (this.rarity != null) item.setRarity(this.rarity);
        if (this.maxStack != -1) item.setMaxStackSize(this.maxStack);

        if (this.modelNamespace != null && this.modelKey != null) {
            item.setItemModel(this.modelNamespace, this.modelKey);
        }

        if (this.foodHunger > 0 || this.foodSaturation > 0 || this.canAlwaysEat) {
            item.setFoodComponent(this.foodHunger, this.foodSaturation, this.canAlwaysEat);
        }

        if (this.trimPattern != null && this.trimMaterial != null) {
            item.setTrimByName(this.trimPattern, this.trimMaterial);
        }

        return item.clearError();
    }

    private String replaceVariables(String text, Map<String, String> variables) {
        if (text == null || variables == null) return text;

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        return text;
    }
}