package ex.nervisking.Placeholder;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholder {

    private static final Map<String, BiFunction<Player, String[], String>> placeholders = new HashMap<>();

    // Versión con parámetros separados por "_"
    private static final Pattern PATTERN_SPLIT = Pattern.compile("%([a-zA-Z0-9]+)(?:_([^%]+))?%");

    // Versión que toma todo el contenido
    private static final Pattern PATTERN_FULL = Pattern.compile("%([^%]+)%");

    public static void registerPlaceholder(String key, BiFunction<Player, String[], String> function) {
        placeholders.put(key, function);
    }

    /** Versión que separa por "_" */
    public static String parsePlaceholdersSplit(Player player, String text) {
        if (text == null || text.isEmpty()) return "";

        Matcher matcher = PATTERN_SPLIT.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);
            String paramString = matcher.group(2);
            String[] params = paramString != null ? paramString.split("_") : new String[0];

            String replacement;
            if (placeholders.containsKey(key)) {
                replacement = placeholders.get(key).apply(player, params);
                if (replacement == null) replacement = "";
            } else {
                replacement = matcher.group(0);
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /** Versión que pasa todo el contenido como un solo argumento */
    public static String parsePlaceholdersFull(Player player, String text) {
        if (text == null || text.isEmpty()) return "";

        Matcher matcher = PATTERN_FULL.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String fullKey = matcher.group(1);
            String replacement;

            if (placeholders.containsKey(fullKey)) {
                replacement = placeholders.get(fullKey).apply(player, new String[]{ fullKey });
                if (replacement == null) replacement = "";
            } else {
                replacement = matcher.group(0);
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
