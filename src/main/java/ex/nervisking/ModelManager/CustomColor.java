package ex.nervisking.ModelManager;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public enum CustomColor {

    BLACK("&0", "#000000"),
    DARK_BLUE("&1", "#0000AA"),
    DARK_GREEN("&2", "#00AA00"),
    DARK_AQUA("&3", "#00AAAA"),
    DARK_RED("&4", "#AA0000"),
    DARK_PURPLE("&5", "#AA00AA"),
    GOLD("&6", "#FFAA00"),
    GRAY("&7", "#AAAAAA"),
    DARK_GRAY("&8", "#555555"),
    BLUE("&9", "#5555FF"),
    GREEN("&a", "#55FF55"),
    AQUA("&b", "#55FFFF"),
    RED("&c", "#FF5555"),
    LIGHT_PURPLE("&d", "#FF55FF"),
    YELLOW("&e", "#FFFF55"),
    WHITE("&f", "#FFFFFF"),

    RANDOM("&r", null) {
        @Override
        public Color getColor() {
            return getRandom().getColor();
        }

        @Override
        public String getHex() {
            return getRandom().getHex();
        }

        @Override
        public String getCode() {
            return "&r";
        }
    };

    private final String code;
    private final String hex;
    private final Color color;

    CustomColor(String code, String hex) {
        this.code = code;
        this.hex = hex;
        if (hex != null) {
            this.color = Color.decode(hex);
        } else {
            this.color = null;
        }
    }

    public Color getColor() {
        return color;
    }

    public String getHex() {
        return hex;
    }

    public String getCode() {
        return code;
    }

    private static final Map<String, CustomColor> codeMap = new HashMap<>();

    static {
        for (CustomColor c : values()) {
            codeMap.put(c.code.toLowerCase(), c);
        }
    }

    public static CustomColor fromCode(String input) {
        return codeMap.get(input.toLowerCase());
    }

    public static CustomColor getRandom() {
        CustomColor[] values = CustomColor.values();
        CustomColor color;
        do {
            color = values[ThreadLocalRandom.current().nextInt(values.length)];
        } while (color == RANDOM);
        return color;
    }

    public static String applyGradientRandom(String text) {
        CustomColor start = getRandom();
        CustomColor end = getRandom();
        while (start == end) {
            end = getRandom();
        }
        return applyGradient(text, start.getColor(), end.getColor());
    }

    public static String applyGradient(String text, Color start, Color end) {
        StringBuilder gradientText = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            double ratio = (double) i / Math.max(1, length - 1);

            int r = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

            String hex = String.format("#%02x%02x%02x", r, g, b);
            gradientText.append(toMinecraftHexColor(hex)).append(text.charAt(i));
        }

        return gradientText.toString();
    }

    public static String toMinecraftHexColor(String hex) {
        StringBuilder builder = new StringBuilder("§x");
        for (char c : hex.substring(1).toCharArray()) {
            builder.append('§').append(c);
        }
        return builder.toString();
    }
}
