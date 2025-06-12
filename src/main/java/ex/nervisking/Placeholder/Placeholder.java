package ex.nervisking.Placeholder;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholder {

    private static final Map<String, BiFunction<Player, String[], String>> placeholders = new HashMap<>();
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([a-zA-Z0-9]+)(?:_([^%]+))?%");

    /**
     * Registra un nuevo placeholder con una función que recibe un jugador y una lista de parámetros.
     * @param key La clave del placeholder (ejemplo: "animation", "counter", "heal").
     * @param function La función que genera el valor del placeholder basado en el jugador y los parámetros.
     */
    public static void registerPlaceholder(String key, BiFunction<Player, String[], String> function) {
        placeholders.put(key, function);
    }

    /**
     * Obtiene el valor de un placeholder dentro de un texto, considerando el jugador si es necesario.
     * @param player El jugador (puede ser null si no se necesita).
     * @param text El texto donde se reemplazarán los placeholders.
     * @return El texto con los placeholders reemplazados.
     */
    public static String parsePlaceholders(Player player, String text) {
        if (text == null || text.isEmpty()) return "";

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1); // Nombre del placeholder (ejemplo: animation, heal, name)
            String paramString = matcher.group(2); // Parámetros separados por "_"
            String[] params = paramString != null ? paramString.split("_") : new String[0];

            if (placeholders.containsKey(key)) {
                String replacement = placeholders.get(key).apply(player, params);
                matcher.appendReplacement(result, replacement);
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
