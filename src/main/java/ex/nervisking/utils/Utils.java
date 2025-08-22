package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.DefaultFontInfo;
import ex.nervisking.ModelManager.Pattern.ToUse;
import ex.nervisking.ModelManager.Plugins;
import ex.nervisking.Placeholder.Placeholder;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class Utils {

    private final Pattern HEX_PATTERN;
    private final MiniMessage MINI_MESSAGE;
    private final LegacyComponentSerializer LEGACY_SERIALIZER;
    private final int SERVER_VERSION;

    public Utils() {
        this.HEX_PATTERN = Pattern.compile("(#|&#)([A-Fa-f0-9]{6})");
        this.MINI_MESSAGE = MiniMessage.miniMessage();
        this.LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
        this.SERVER_VERSION = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
    }

    public String getPrefix() {
        return ExApi.getPrefix();
    }

    @ToUse(
            description = {"Convierte un texto con códigos de color a un formato compatible con MiniMessage y los aplica.",
            "Si el texto contiene códigos de color antiguos (&a, &f, etc.) o hexadecimales (&#ffffff, #ffffff),",
            "los convierte a un formato compatible con MiniMessage."},
            params = "text -> Texto a procesar.",
            returns = "Texto procesado y coloreado."
    )
    public String setColoredMessage(@NotNull String text) {
        if (text.isEmpty()) return "";

        text = replacePlaceholders(null, text);
        text = processText(text);
        boolean containsLegacyFormat = text.contains("&") || text.contains("§");
        if (containsLegacyFormat && SERVER_VERSION < 16) {
            return ChatColor.translateAlternateColorCodes('&', text);
        }

        text = transformLegacyHex(text);
        text = containsLegacyFormat ? formatMiniMessage(text) : text;
        return LEGACY_SERIALIZER.serialize(MINI_MESSAGE.deserialize(text));
    }

    private @NotNull String formatMiniMessage(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        TextComponent textComponent = LEGACY_SERIALIZER.deserialize(text);
        String serialized = MINI_MESSAGE.serialize(textComponent.compact());
        return serialized.replace("\\", "");
    }

    private String transformLegacyHex(String text) {
        return HEX_PATTERN.matcher(text).replaceAll(matcher -> "<color:#" + matcher.group(2) + ">");
    }

    @ToUse(
            value = "Reemplaza los placeholders del texto y los placeholders de PlaceholderAPI.",
            params = {"player -> Jugador al que se le aplican los placeholders.",
            "text -> Texto a procesar."},
            returns = "Texto con los placeholders reemplazados."
    )
    public String setPlaceholders(final Player player, String text) {
        text = replacePlaceholders(player, text);
        text = Placeholder.parsePlaceholdersSplit(player, text);
        text = Placeholder.parsePlaceholdersFull(player, text);
        if (!ExApi.isPlugin(Plugins.PLACEHOLDERAPI)) {
            return setColoredMessage(text);
        }
        return setColoredMessage(PlaceholderAPI.setPlaceholders(player, text));
    }

    @ToUse
    public String getPlaceholders(final Player player, String text){
        if (ExApi.isPlugin(Plugins.PLACEHOLDERAPI)) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }

    @ToUse(
            value = "Centra un mensaje en el chat.",
            params = "message -> Mensaje a centrar.",
            returns = "Mensaje centrado."
    )
    public String getCenteredMessage(@NotNull String message){
        if (message.isEmpty()) return "";
        int CENTER_PX = 154;
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
            }else if(previousCode){
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return (sb + message);
    }

    @ToUse(
            value = "Formatea un número grande a un formato legible con sufijos.",
            description = "Ejemplos: 1500 -> 1.5K, 2500000 -> 2.5M, 1000000000 -> 1B",
            params = "amount -> Número a formatear.",
            returns = "String formateado."
    )
    public String format(double amount) {
        String[] suffixes = new String[]{"", "K", "M", "B", "T", "P"};
        int index = 0;

        while (amount >= 1000 && index < suffixes.length - 1) {
            amount /= 1000;
            index++;
        }

        DecimalFormat df = (amount % 1 == 0) ? new DecimalFormat("#,##0") : new DecimalFormat("#,##0.##");
        return df.format(amount) + suffixes[index];
    }

    private @NotNull String processText(@NotNull String text) {
        if (text.isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < text.length()) {
            if (text.startsWith("[/", i)) {
                int endBracket = text.indexOf("]", i);
                if (endBracket == -1) {
                    result.append(text.substring(i));
                    break;
                }

                String insideBrackets = getString(text, i, endBracket);
                result.append(insideBrackets);

                i = endBracket + 1;
            } else {
                result.append(text.charAt(i));
                i++;
            }
        }
        return result.toString();
    }

    private String getString(@NotNull String text, int i, int endBracket) {
        // Extraemos el texto entre corchetes, eliminando la parte "[/" y "]"
        String insideBrackets = text.substring(i + 2, endBracket);
        char lastChar = text.charAt(endBracket - 1);  // Verificamos el último carácter antes de ']'

        if (lastChar == '+' || lastChar == '-') {
            insideBrackets = insideBrackets.replaceAll("[+-]", "").trim();  // Eliminamos los signos + y -
            if (lastChar == '+') {
                insideBrackets = insideBrackets.toUpperCase();  // Convertimos a mayúsculas si es '+'
            } else {
                insideBrackets = insideBrackets.toLowerCase();  // Convertimos a minúsculas si es '-'
            }
        }
        return insideBrackets;
    }

    @ToUse
    public String applyGradient(@NotNull String text, Color start, Color end) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            double ratio = (double) i / (text.length() - 1);
            int r = (int) (start.getRed() * (1 - ratio) + end.getRed() * ratio);
            int g = (int) (start.getGreen() * (1 - ratio) + end.getGreen() * ratio);
            int b = (int) (start.getBlue() * (1 - ratio) + end.getBlue() * ratio);
            String hex = String.format("#%02x%02x%02x", r, g, b);
            builder.append(ChatColor.of(hex)).append(text.charAt(i));
        }

        return builder.toString();
    }

    // 🔹 Placeholders globales (no dependen de jugador)
    private static final Map<String, Supplier<String>> GLOBAL_PLACEHOLDERS = new HashMap<>();

    static {
        GLOBAL_PLACEHOLDERS.put("%prefix%", ExApi::getPrefix);
        GLOBAL_PLACEHOLDERS.put("%online_player%", () -> String.valueOf(Bukkit.getOnlinePlayers().size()));
        GLOBAL_PLACEHOLDERS.put("%max_player%", () -> String.valueOf(Bukkit.getMaxPlayers()));
    }

    /**
     * Genera un mapa de placeholders para un jugador específico
     */
    private @NotNull Map<String, Supplier<String>> getPlayerPlaceholders(@NotNull Player player) {
        Map<String, Supplier<String>> map = new HashMap<>();
        map.put("%player%", player::getName);
        map.put("%display_name%", player::getDisplayName);
        map.put("%world%", () -> player.getWorld().getName());
        map.put("%heal%", () -> String.valueOf(player.getHealth()));
        map.put("%food%", () -> String.valueOf(player.getFoodLevel()));
        map.put("%heal_bar%", () -> createBar((int) Math.ceil(player.getHealth()), "❤", "&#b90000", "&#ff4d4d"));
        map.put("%food_bar%", () -> createBar(player.getFoodLevel(), "🍗", "&#995c00", "&#ff9933"));
        map.put("%kills%", () -> String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS)));
        map.put("%deaths%", () -> String.valueOf(player.getStatistic(Statistic.DEATHS)));
        map.put("%ping%", () -> getPing(player));
        map.put("%x%", () -> String.format("%.0f", player.getLocation().getX()));
        map.put("%y%", () -> String.format("%.0f", player.getLocation().getY()));
        map.put("%z%", () -> String.format("%.0f", player.getLocation().getZ()));
        return map;
    }

    /**
     * Reemplaza los placeholders definidos (globales + jugador)
     */
    private String replacePlaceholders(Player player, String message) {
        if (message == null || message.isEmpty()) return "";

        // 🌍 Placeholders globales
        for (Map.Entry<String, Supplier<String>> entry : GLOBAL_PLACEHOLDERS.entrySet()) {
            if (message.contains(entry.getKey())) {
                message = message.replace(entry.getKey(), safeString(entry.getValue().get()));
            }
        }

        // 👤 Placeholders de jugador
        if (player != null) {
            for (Map.Entry<String, Supplier<String>> entry : getPlayerPlaceholders(player).entrySet()) {
                if (message.contains(entry.getKey())) {
                    message = message.replace(entry.getKey(), safeString(entry.getValue().get()));
                }
            }
        }

        return message;
    }

    private @NotNull String getPing(Player player) {
        int ping;
        try {
            ping = player.getPing();
        } catch (Exception e) {
            ping = 0;
        }

        return String.valueOf(ping);
    }

    @Contract(value = "!null -> param1", pure = true)
    private @NotNull String safeString(String value) {
        return value != null ? value : "N/A";
    }

    public String createBar(int valor, String symbol, String fullColor, String midColor) {
        int total = 10; // Número de iconos en la barra
        int amount = (int) ((double) valor / 20 * total);
        int midPoint = total / 2;

        StringBuilder bar = new StringBuilder(); // Borde de la barra

        for (int i = 0; i < total; i++) {
            if (i < amount) {
                String color = (i < midPoint) ? fullColor : midColor;
                bar.append(color).append(symbol);
            } else {
                bar.append("&#ffffff").append(symbol);
            }
        }

        return bar.toString();
    }
    public String formatTime(long time, boolean fromMillis) {
        return FormatTime.formatTime(time, fromMillis, FormatTime.TimeFormatType.DIGITAL);
    }

    @ToUse
    public String formatTime(long seconds) {
        return FormatTime.formatTime(seconds, FormatTime.TimeFormatType.DIGITAL);
    }

    @ToUse
    public String formatTime(long time, boolean fromMillis, FormatTime.TimeFormatType timeFormatType) {
        return FormatTime.formatTime(time, fromMillis, timeFormatType);
    }

    @ToUse
    public String formatTime(long timeSeconds, FormatTime.TimeFormatType timeFormatType) {
        return FormatTime.formatTime(timeSeconds, timeFormatType);
    }

    @ToUse(
            value = "Convierte un string de tiempo personalizado a milisegundos.",
            params = {
                    "timeString: String con el tiempo a convertir. Ejemplos: \"1d10m\", \"5m;35s;4t\", \"1h 30m 20s\", \"500ms\""
            },
            returns = "Tiempo en milisegundos.",
            usedFor = "Parseo flexible de tiempos para configuraciones o temporizadores.",
            notes = {
                    "Soporta unidades: ms (milisegundos), t (ticks = 50ms), s (segundos), m (minutos), h (horas), d (días).",
                    "Lanza NumberFormatException si el formato o unidad es inválida.",
                    "Permite concatenar múltiples unidades sin espacios, ej: \"1d10m\", \"500ms\"."
            }
    )
    public long parseTime(@NotNull String timeString) throws NumberFormatException {
        if (timeString.isEmpty()) return 0L;
        long totalMillis = 0;
        StringBuilder numberBuffer = new StringBuilder();
        StringBuilder unitBuffer = new StringBuilder();

        String input = timeString.replace(" ", "").replace(";", "").replace(",", "").toLowerCase();
        int length = input.length();

        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);

            if (Character.isDigit(c)) {
                if (unitBuffer.length() > 0) {
                    totalMillis += convertToMillis(numberBuffer.toString(), unitBuffer.toString());
                    numberBuffer.setLength(0);
                    unitBuffer.setLength(0);
                }
                numberBuffer.append(c);
            } else {
                unitBuffer.append(c);
                boolean nextIsLetter = (i + 1 < length) && Character.isLetter(input.charAt(i + 1));
                if (!nextIsLetter || unitBuffer.length() == 2) {
                    if (numberBuffer.length() == 0) {
                        throw new NumberFormatException("Formato inválido: número faltante antes de unidad '" + unitBuffer + "'");
                    }
                    totalMillis += convertToMillis(numberBuffer.toString(), unitBuffer.toString());
                    numberBuffer.setLength(0);
                    unitBuffer.setLength(0);
                }
            }
        }

        if (numberBuffer.length() > 0) {
            throw new NumberFormatException("Formato inválido: falta unidad para " + numberBuffer);
        }

        return totalMillis;
    }

    private long convertToMillis(String numberStr, String unit) throws NumberFormatException {
        long value = Long.parseLong(numberStr);
        return switch (unit) {
            case "ms" -> value;
            case "t" -> value * 50;
            case "s" -> value * 1000L;
            case "m" -> value * 60_000L;
            case "h" -> value * 3_600_000L;
            case "d" -> value * 86_400_000L;
            default -> throw new NumberFormatException("Unidad de tiempo no válida: '" + unit + "'");
        };
    }

    @ToUse(
            value = "Verifica si un jugador cumple con las condiciones de visualización.",
            params = {"player -> El jugador a verificar.", "condition -> La condición a evaluar."},
            returns = "true si el jugador cumple con la condición, false si no."
    )
    public boolean conditions(Player player, String condition) {
        if (condition == null || condition.isEmpty()) {
            return true;  // No hay condición, siempre se muestra
        }

        String[] conditionList = condition.split(";");
        return Arrays.stream(conditionList).anyMatch(c -> evaluateCondition(player, c));
    }

    private boolean evaluateCondition(Player player, String condition) {
        // Dividimos la condición usando los posibles operadores: ==, !=, <, >, >=, <=
        String[] operators = {"==", "!=", "<", ">", ">=", "<="};

        for (String operator : operators) {
            if (condition.contains(operator)) {
                // Usamos la expresión regular para dividir la condición
                String[] parts = condition.split("\\%s".formatted(operator)); // Escapamos el operador correctamente

                if (parts.length == 2) {

                    String leftValue = setPlaceholders(player, parts[0].trim());
                    String rightValue = setPlaceholders(player, parts[1].trim());

                    try {
                        int leftInt = Integer.parseInt(leftValue);
                        int rightInt = Integer.parseInt(rightValue);

                        switch (operator) {
                            case "==":
                                return leftInt == rightInt;
                            case "!=":
                                return leftInt != rightInt;
                            case "<":
                                return leftInt < rightInt;
                            case ">":
                                return leftInt > rightInt;
                            case ">=":
                                return leftInt >= rightInt;
                            case "<=":
                                return leftInt <= rightInt;
                        }
                    } catch (NumberFormatException e) {
                        return switch (operator) {
                            case "==" -> leftValue.equals(rightValue);
                            case "!=" -> !leftValue.equals(rightValue);
                            default -> false; // Para otros operadores, no lo permitimos
                        };
                    }
                }
            }
        }
        return false;
    }

    @ToUse(
            value = "Verifica si un texto contiene códigos de color",
            description = {"- Legacy (&a, &f, etc.)",
                    "- Hexadecimales (#ffffff o &#ffffff)",
                    "- MiniMessage (<red>, <gradient:...>, etc.)"},
            params = "text -> Texto a analizar.",
            returns = "true si hay códigos de color/estilo, false si no."
    )
    public boolean hasColorCodes(String text) {
        if (text == null) return false;

        String hexPattern = "(?i)(&#|#)[0-9a-f]{6}";
        String legacyPattern = "(?i)&[0-9a-fk-or]";
        String miniMessagePattern = "<[^>]+>"; // Cualquier etiqueta <...>

        return text.matches(".*" + hexPattern + ".*")
                || text.matches(".*" + legacyPattern + ".*")
                || text.matches(".*" + miniMessagePattern + ".*");
    }

    @ToUse(value = "Elimina todos los códigos de color (legacy, hex, MiniMessage).")
    public String removeColorCodes(String input) {
        if (input == null) return "";

        return input
                // Quitar hex (#ffffff o &#ffffff)
                .replaceAll("(?i)(&#|#)[0-9a-f]{6}", "")
                // Quitar legacy (&a, &f, etc.)
                .replaceAll("(?i)&[0-9a-fk-or]", "")
                // Quitar etiquetas MiniMessage (<red>, <bold>, etc.)
                .replaceAll("<[^>]+>", "");
    }

    public boolean isValidText(String input, String regex, Integer minLength, Integer maxLength) {
        if (input == null || input.isEmpty()) return false;
        // Valores por defecto
        String pattern = regex != null ? regex : "^[a-zA-Z0-9_\\- ]+$";
        int min = minLength != null ? minLength : 3;
        int max = maxLength != null ? maxLength : 16;

        // Validar longitud
        if (input.length() < min || input.length() > max) {
            return true;
        }

        // Validar regex
        return !input.matches(pattern);
    }

    @ToUse
    public boolean isValidText(String input) {
        return this.isValidText(input, null, null, null);
    }

    @ToUse
    public boolean isValidText(String input, String regex) {
        return this.isValidText(input, regex, null, null);
    }

    @ToUse
    public boolean isValidText(String input, String regex, Integer minLength) {
        return this.isValidText(input, regex, minLength, null);
    }

    @ToUse
    public boolean isValidText(String input, String regex, int maxLength) {
        return this.isValidText(input, regex, null, maxLength);
    }

    public String getValidatedText(String input, String regex, Integer minLength, Integer maxLength) {
        if (input == null || input.isEmpty()) return "";

        String pattern = regex != null ? regex : "^[a-zA-Z0-9_\\- ]+$";
        int min = minLength != null ? minLength : 3;
        int max = maxLength != null ? maxLength : 16;

        return (input.length() >= min && input.length() <= max && input.matches(pattern)) ? input : "";
    }

    @ToUse
    public String getValidatedText(String input, String regex, Integer maxLength) {
        return this.getValidatedText(input, regex, null, maxLength);
    }

    @ToUse
    public String getValidatedText(String input, String regex, int minLength) {
        return this.getValidatedText(input, regex, minLength, null);
    }

    @ToUse
    public String getValidatedText(String input, String regex) {
        return this.getValidatedText(input, regex, null, null);
    }

    @ToUse
    public String getValidatedText(String input) {
        return this.getValidatedText(input, null, null, null);
    }

    @ToUse
    public String getLocationString(Location location) {
        if (location == null) return "no data";
        return String.format("X: %.2f, Y: %.2f, Z: %.2f, World: %s", location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
    }
}