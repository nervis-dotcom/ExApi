package ex.nervisking.colors;

import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public enum Colors {

    BLACK("#000000"),
    DARK_BLUE("#0000AA"),
    DARK_GREEN("#00AA00"),
    DARK_AQUA("#00AAAA"),
    DARK_RED("#AA0000"),
    DARK_PURPLE("#AA00AA"),
    GOLD("#FFAA00"),
    GRAY("#AAAAAA"),
    DARK_GRAY("#555555"),
    BLUE("#5555FF"),
    GREEN("#55FF55"),
    AQUA("#55FFFF"),
    RED("#FF5555"),
    LIGHT_PURPLE("#FF55FF"),
    PINK("#FF55FF"),
    YELLOW("#FFFF55"),
    WHITE("#FFFFFF"),
    BROWN("#804000");

    private final String hex;

    Colors(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

    public ChatColor toChatColor() {
        return ChatColor.of(hex);
    }
}
