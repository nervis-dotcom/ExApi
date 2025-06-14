package ex.nervisking.itemsManager;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum RDMaterial {

    WOOL(Arrays.asList(
            Material.WHITE_WOOL, Material.ORANGE_WOOL, Material.MAGENTA_WOOL, Material.LIGHT_BLUE_WOOL,
            Material.YELLOW_WOOL, Material.LIME_WOOL, Material.PINK_WOOL, Material.GRAY_WOOL,
            Material.LIGHT_GRAY_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.BLUE_WOOL,
            Material.BROWN_WOOL, Material.GREEN_WOOL, Material.RED_WOOL, Material.BLACK_WOOL
    )),
    CARPET(Arrays.asList(
            Material.WHITE_CARPET, Material.ORANGE_CARPET, Material.MAGENTA_CARPET, Material.LIGHT_BLUE_CARPET,
            Material.YELLOW_CARPET, Material.LIME_CARPET, Material.PINK_CARPET, Material.GRAY_CARPET,
            Material.LIGHT_GRAY_CARPET, Material.CYAN_CARPET, Material.PURPLE_CARPET, Material.BLUE_CARPET,
            Material.BROWN_CARPET, Material.GREEN_CARPET, Material.RED_CARPET, Material.BLACK_CARPET
    )),
    TERRACOTTA(Arrays.asList(
            Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA,
            Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA, Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA,
            Material.LIGHT_GRAY_TERRACOTTA, Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.BLUE_TERRACOTTA,
            Material.BROWN_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA, Material.BLACK_TERRACOTTA
    )),
    BANNER(Arrays.asList(
            Material.WHITE_BANNER, Material.ORANGE_BANNER, Material.MAGENTA_BANNER, Material.LIGHT_BLUE_BANNER,
            Material.YELLOW_BANNER, Material.LIME_BANNER, Material.PINK_BANNER, Material.GRAY_BANNER,
            Material.LIGHT_GRAY_BANNER, Material.CYAN_BANNER, Material.PURPLE_BANNER, Material.BLUE_BANNER,
            Material.BROWN_BANNER, Material.GREEN_BANNER, Material.RED_BANNER, Material.BLACK_BANNER
    )),
    CANDLE(Arrays.asList(
            Material.WHITE_CANDLE, Material.ORANGE_CANDLE, Material.MAGENTA_CANDLE, Material.LIGHT_BLUE_CANDLE,
            Material.YELLOW_CANDLE, Material.LIME_CANDLE, Material.PINK_CANDLE, Material.GRAY_CANDLE,
            Material.LIGHT_GRAY_CANDLE, Material.CYAN_CANDLE, Material.PURPLE_CANDLE, Material.BLUE_CANDLE,
            Material.BROWN_CANDLE, Material.GREEN_CANDLE, Material.RED_CANDLE, Material.BLACK_CANDLE
    )),
    GLASS(Arrays.asList(
            Material.WHITE_STAINED_GLASS, Material.ORANGE_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS,
            Material.YELLOW_STAINED_GLASS, Material.LIME_STAINED_GLASS, Material.PINK_STAINED_GLASS, Material.GRAY_STAINED_GLASS,
            Material.LIGHT_GRAY_STAINED_GLASS, Material.CYAN_STAINED_GLASS, Material.PURPLE_STAINED_GLASS, Material.BLUE_STAINED_GLASS,
            Material.BROWN_STAINED_GLASS, Material.GREEN_STAINED_GLASS, Material.RED_STAINED_GLASS, Material.BLACK_STAINED_GLASS
    )),
    DYE(Arrays.asList(
            Material.BLACK_DYE, Material.BLUE_DYE, Material.BROWN_DYE, Material.CYAN_DYE, Material.GRAY_DYE, Material.GREEN_DYE,
            Material.LIGHT_BLUE_DYE, Material.LIGHT_GRAY_DYE, Material.LIME_DYE, Material.MAGENTA_DYE, Material.ORANGE_DYE,
            Material.PINK_DYE, Material.PURPLE_DYE, Material.RED_DYE, Material.WHITE_DYE, Material.YELLOW_DYE
    ));

    private final List<Material> materials;
    private final Random random = new Random();

    RDMaterial(List<Material> materials) {
        this.materials = materials;
    }

    public Material getRandom() {
        return materials.get(random.nextInt(materials.size()));
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public static RDMaterial fromString(String input) {
        return switch (input.toLowerCase()) {
            case "wool" -> WOOL;
            case "carpet" -> CARPET;
            case "terracotta" -> TERRACOTTA;
            case "banner" -> BANNER;
            case "candle" -> CANDLE;
            case "glass" -> GLASS;
            case "dye" -> DYE;
            default -> null;
        };
    }

    public static RDMaterial fromKey(RDMaterial input) {
        return input;
    }
}
