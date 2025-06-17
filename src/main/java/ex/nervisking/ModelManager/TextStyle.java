package ex.nervisking.ModelManager;

public enum TextStyle {

    NONE,
    SMALLCAPS,
    ACCENT,
    BIG,
    BUBBLE,
    CURRENCY;

    public static TextStyle fromString(String name) {
        try {
            return TextStyle.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
