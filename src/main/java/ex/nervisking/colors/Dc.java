package ex.nervisking.colors;

import com.google.common.base.Preconditions;
import java.awt.Color;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Dc {

    public static final char COLOR_CHAR = '§';
    public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '§' + "[0-9A-FK-ORX]");
    private static final Map<Character, Dc> BY_CHAR = new HashMap<>();
    private static final Map<String, Dc> BY_NAME = new HashMap<>();
    public static final Dc BLACK = new Dc('0', "black", new Color(0));
    public static final Dc DARK_BLUE = new Dc('1', "dark_blue", new Color(170));
    public static final Dc DARK_GREEN = new Dc('2', "dark_green", new Color(43520));
    public static final Dc DARK_AQUA = new Dc('3', "dark_aqua", new Color(43690));
    public static final Dc DARK_RED = new Dc('4', "dark_red", new Color(11141120));
    public static final Dc DARK_PURPLE = new Dc('5', "dark_purple", new Color(11141290));
    public static final Dc GOLD = new Dc('6', "gold", new Color(16755200));
    public static final Dc GRAY = new Dc('7', "gray", new Color(11184810));
    public static final Dc DARK_GRAY = new Dc('8', "dark_gray", new Color(5592405));
    public static final Dc BLUE = new Dc('9', "blue", new Color(5592575));
    public static final Dc GREEN = new Dc('a', "green", new Color(5635925));
    public static final Dc AQUA = new Dc('b', "aqua", new Color(5636095));
    public static final Dc RED = new Dc('c', "red", new Color(16733525));
    public static final Dc LIGHT_PURPLE = new Dc('d', "light_purple", new Color(16733695));
    public static final Dc YELLOW = new Dc('e', "yellow", new Color(16777045));
    public static final Dc WHITE = new Dc('f', "white", new Color(16777215));
    public static final Dc MAGIC = new Dc('k', "obfuscated");
    public static final Dc BOLD = new Dc('l', "bold");
    public static final Dc STRIKETHROUGH = new Dc('m', "strikethrough");
    public static final Dc UNDERLINE = new Dc('n', "underline");
    public static final Dc ITALIC = new Dc('o', "italic");
    public static final Dc RESET = new Dc('r', "reset");
    private static int count = 0;
    private final String toString;
    private final String name;
    private final int ordinal;
    private final Color color;

    private Dc(char code, String name) {
        this(code, name, (Color)null);
    }

    private Dc(char code, String name, Color color) {
        this.name = name;
        this.toString = new String(new char[]{'§', code});
        this.ordinal = count++;
        this.color = color;
        BY_CHAR.put(code, this);
        BY_NAME.put(name.toUpperCase(Locale.ROOT), this);
    }

    private Dc(String name, String toString, int rgb) {
        this.name = name;
        this.toString = toString;
        this.ordinal = -1;
        this.color = new Color(rgb);
    }

    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.toString);
        return hash;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            Dc other = (Dc)obj;
            return Objects.equals(this.toString, other.toString);
        } else {
            return false;
        }
    }

    public String toString() {
        return this.toString;
    }

    public static String stripColor(String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    public static Dc getByChar(char code) {
        return BY_CHAR.get(code);
    }

    public static Dc of(Color color) {
        return of("#" + String.format("%08x", color.getRGB()).substring(2));
    }

    public static Dc of(String string) {
        Preconditions.checkArgument(string != null, "string cannot be null");
        if (string.startsWith("#") && string.length() == 7) {
            int rgb;
            try {
                rgb = Integer.parseInt(string.substring(1), 16);
            } catch (NumberFormatException var7) {
                throw new IllegalArgumentException("Illegal hex string " + string);
            }

            StringBuilder magic = new StringBuilder("§x");

            for(char c : string.substring(1).toCharArray()) {
                magic.append('§').append(c);
            }

            return new Dc(string, magic.toString(), rgb);
        } else {
            Dc defined = BY_NAME.get(string.toUpperCase(Locale.ROOT));
            if (defined != null) {
                return defined;
            } else {
                throw new IllegalArgumentException("Could not parse ChatColor " + string);
            }
        }
    }

    public static Dc valueOf(String name) {
        Preconditions.checkNotNull(name, "Name is null");
        Dc defined = BY_NAME.get(name);
        Preconditions.checkArgument(defined != null, "No enum constant " + Dc.class.getName() + "." + name);
        return defined;
    }

    public static Dc[] values() {
        return BY_CHAR.values().toArray(new Dc[0]);
    }

    public String name() {
        return this.getName().toUpperCase(Locale.ROOT);
    }

    public int ordinal() {
        Preconditions.checkArgument(this.ordinal >= 0, "Cannot get ordinal of hex color");
        return this.ordinal;
    }

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }
}
