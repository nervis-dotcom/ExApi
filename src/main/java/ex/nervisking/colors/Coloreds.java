package ex.nervisking.colors;

import net.md_5.bungee.api.ChatColor;

public class Coloreds {

    @SuppressWarnings("deprecation")
    public static ChatColor parseColor(Colors color) {
        return switch (color) {
            case BLACK -> ChatColor.of("#000000"); // Negro
            case DARK_BLUE -> ChatColor.of("#0000AA"); // Azul oscuro
            case DARK_GREEN -> ChatColor.of("#00AA00"); // Verde oscuro
            case DARK_AQUA -> ChatColor.of("#00AAAA"); // Aqua oscuro
            case DARK_RED -> ChatColor.of("#AA0000"); // Rojo oscuro
            case DARK_PURPLE -> ChatColor.of("#AA00AA"); // Púrpura oscuro
            case GOLD -> ChatColor.of("#FFAA00"); // Dorado
            case GRAY -> ChatColor.of("#AAAAAA"); // Gris claro
            case DARK_GRAY -> ChatColor.of("#555555"); // Gris oscuro
            case BLUE -> ChatColor.of("#5555FF"); // Azul
            case GREEN -> ChatColor.of("#55FF55"); // Verde claro
            case AQUA -> ChatColor.of("#55FFFF"); // Aqua
            case RED -> ChatColor.of("#FF5555"); // Rojo claro
            case LIGHT_PURPLE -> ChatColor.of("#FF55FF"); // Púrpura claro
            case YELLOW -> ChatColor.of("#FFFF55"); // Amarillo
            case WHITE -> ChatColor.of("#FFFFFF"); // Blanco
            case BROWN -> ChatColor.of("#804000"); // maron
            case PINK -> ChatColor.of("#ff0080"); // rosado
        };
    }

    public static Colors fromAlias(String input) {
        if (input == null) return null;
        String s = input.trim().toLowerCase();

        // 1) si es un código &x
        if (s.startsWith("&") && s.length() == 2) {
            char code = s.charAt(1);
            switch (code) {
                case '0' ->      { return Colors.BLACK; }
                case '1' ->      { return Colors.DARK_BLUE; }
                case '2' ->      { return Colors.DARK_GREEN; }
                case '3' ->      { return Colors.DARK_AQUA; }
                case '4' ->      { return Colors.DARK_RED; }
                case '5' ->      { return Colors.DARK_PURPLE; }
                case '6' ->      { return Colors.GOLD; }
                case '7' ->      { return Colors.GRAY; }
                case '8' ->      { return Colors.DARK_GRAY; }
                case '9' ->      { return Colors.BLUE; }
                case 'a','A' ->  { return Colors.GREEN; }
                case 'b','B' ->  { return Colors.AQUA; }
                case 'c','C' ->  { return Colors.RED; }
                case 'd','D' ->  { return Colors.LIGHT_PURPLE; }
                case 'e','E' ->  { return Colors.YELLOW; }
                case 'f','F' ->  { return Colors.WHITE; }
                case 'h','H' ->  { return Colors.PINK; }
                case 'j','J' ->  { return Colors.BROWN; }
            }
        }

        // 2) si es solo la letra/número sin '&'
        if (s.length() == 1) {
            return fromAlias("&" + s);
        }

        // 3) nombres en español e inglés
        return switch (s) {
            case "negro", "black"                   -> Colors.BLACK;
            case "azul_oscuro", "dark_blue"         -> Colors.DARK_BLUE;
            case "verde_oscuro", "dark_green"       -> Colors.DARK_GREEN;
            case "aqua_oscuro", "dark_aqua"         -> Colors.DARK_AQUA;
            case "rojo_oscuro", "dark_red"          -> Colors.DARK_RED;
            case "morado_oscuro", "dark_purple"     -> Colors.DARK_PURPLE;
            case "dorado", "gold"                   -> Colors.GOLD;
            case "gris_claro", "gray"               -> Colors.GRAY;
            case "gris_oscuro", "dark_gray"         -> Colors.DARK_GRAY;
            case "azul", "blue"                     -> Colors.BLUE;
            case "verde", "green"                   -> Colors.GREEN;
            case "aqua", "cyan"                     -> Colors.AQUA;
            case "rojo", "red"                      -> Colors.RED;
            case "morado_claro", "light_purple"     -> Colors.LIGHT_PURPLE;
            case "rosado", "pink"                   -> Colors.PINK;
            case "amarillo", "yellow"               -> Colors.YELLOW;
            case "blanco", "white"                  -> Colors.WHITE;
            case "marron", "cafe", "brown"          -> Colors.BROWN;
            default                                 -> null;
        };
    }
}
