package ex.nervisking.ModelManager;

public enum TextStyle {


    GOTHIC, SCRIPT, DOUBLE_STRUCK, CIRCLED, SQUARED, BOLD, ITALIC, SANS_SERIF, RUNIC, ARABIC, STARS, HEARTS;


    TextStyle() {
    }

    public static TextStyle fromString(String name) {
        try {
            return TextStyle.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
