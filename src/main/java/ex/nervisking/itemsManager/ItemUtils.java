package ex.nervisking.itemsManager;

import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class ItemUtils {

    public static TrimMaterial getTrimMaterial(String input) {
        if (input.equalsIgnoreCase("AMETHYST")) {
            return TrimMaterial.AMETHYST;
        } else if (input.equalsIgnoreCase("COPPER")) {
            return TrimMaterial.COPPER;
        } else if (input.equalsIgnoreCase("DIAMOND")) {
            return TrimMaterial.DIAMOND;
        } else if (input.equalsIgnoreCase("GOLD")) {
            return TrimMaterial.GOLD;
        } else if (input.equalsIgnoreCase("IRON")) {
            return TrimMaterial.IRON;
        }else if (input.equalsIgnoreCase("EMERALD")) {
            return TrimMaterial.EMERALD;
        }else if (input.equalsIgnoreCase("LAPIS")) {
            return TrimMaterial.LAPIS;
        }else if (input.equalsIgnoreCase("NETHERITE")) {
            return TrimMaterial.NETHERITE;
        }else if (input.equalsIgnoreCase("QUARTZ")) {
            return TrimMaterial.QUARTZ;
        }else if (input.equalsIgnoreCase("REDSTONE")) {
            return TrimMaterial.REDSTONE;
        }else if (input.equalsIgnoreCase("RESIN")) {
            return TrimMaterial.RESIN;
        }
        return null;
    }

    public static TrimPattern getTrimPattern(String input) {
        if (input.equalsIgnoreCase("DUNE")) {
            return TrimPattern.DUNE;
        } else if (input.equalsIgnoreCase("BOLT")) {
            return TrimPattern.BOLT;
        } else if (input.equalsIgnoreCase("EYE")) {
            return TrimPattern.EYE;
        } else if(input.equalsIgnoreCase("COAST")) {
            return TrimPattern.COAST;
        } else if(input.equalsIgnoreCase("RIB")) {
            return TrimPattern.RIB;
        } else if(input.equalsIgnoreCase("FLOW")) {
            return TrimPattern.FLOW;
        }  else if(input.equalsIgnoreCase("HOST")) {
            return TrimPattern.HOST;
        } else if(input.equalsIgnoreCase("RAISER")) {
            return TrimPattern.RAISER;
        } else if(input.equalsIgnoreCase("SHAPER")) {
            return TrimPattern.SHAPER;
        } else if(input.equalsIgnoreCase("SILENCE")) {
            return TrimPattern.SILENCE;
        } else if(input.equalsIgnoreCase("TIDE")) {
            return TrimPattern.TIDE;
        } else if(input.equalsIgnoreCase("VEX")) {
            return TrimPattern.VEX;
        } else if(input.equalsIgnoreCase("WARD")) {
            return TrimPattern.WARD;
        } else if(input.equalsIgnoreCase("WAYFINDER")) {
            return TrimPattern.WAYFINDER;
        } else if(input.equalsIgnoreCase("WILD")) {
            return TrimPattern.WILD;
        }
        return null;
    }
}
