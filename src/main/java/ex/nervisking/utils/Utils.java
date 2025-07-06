package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.DefaultFontInfo;
import ex.nervisking.ModelManager.Plugins;
import ex.nervisking.Placeholder.Placeholder;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class Utils {

    private final Pattern HEX_PATTERN = Pattern.compile("(#|&#)([A-Fa-f0-9]{6})");
    private final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    private final int SERVER_VERSION = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);

    /**
     * Obtiene el prefijo del plugin ExApi.
     * @return El prefijo del plugin.
     */
    public String getPrefix() {
        return ExApi.getPrefix();
    }

    /**
     * Convierte un texto con códigos de color a un componente de texto de BungeeCord.
     * Si el texto contiene códigos de color antiguos (&a, &f, etc.) o hexadecimales (&#ffffff, #ffffff),
     * los convierte a un formato compatible con MiniMessage y los aplica.
     * @param text Texto a procesar.
     * @return Componente de texto de BungeeCord con los colores aplicados.
     */
    @Deprecated(since = "1.0.0")
    public net.md_5.bungee.api.chat.TextComponent setColoredComponent(@NotNull String text) {
        String colored = setColoredMessage(text);
        net.md_5.bungee.api.chat.TextComponent base = new net.md_5.bungee.api.chat.TextComponent();
        BaseComponent[] converted = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(colored);

        for (BaseComponent comp : converted) {
            base.addExtra(comp);
        }

        return base;
    }

    /**
     * Convierte un texto con códigos de color a un formato compatible con MiniMessage y los aplica.
     * Si el texto contiene códigos de color antiguos (&a, &f, etc.) o hexadecimales (&#ffffff, #ffffff),
     * los convierte a un formato compatible con MiniMessage.
     *
     * @param text Texto a procesar.
     * @return Texto procesado y coloreado.
     */
    public String setColoredMessage(String text) {
        if (text == null || text.isEmpty()) return "";

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

    /**
     * Reemplaza los placeholders del texto y los placeholders de PlaceholderAPI.
     * @param player Jugador al que se le aplican los placeholders.
     * @param text Texto a procesar.
     * @return Texto con los placeholders reemplazados.
     */
    public String setPlaceholders(final Player player, String text) {
        text = replacePlaceholders(player, text);
        text = Placeholder.parsePlaceholders(player, text);
        if (!ExApi.isPlugin(Plugins.PLACEHOLDERAPI)) {
            return setColoredMessage(text);
        }
        return setColoredMessage(PlaceholderAPI.setPlaceholders(player, text));
    }

    /**
     * Centra un mensaje en el chat.
     * @param message Mensaje a centrar.
     * @return Mensaje centrado.
     */
    public String getCenteredMessage(String message){
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

    /**
     * Formatea un número grande a un formato legible con sufijos.
     * Ejemplos: 1500 -> "1.5K", 2500000 -> "2.5M", 1000000000 -> "1B"
     *
     * @param amount Número a formatear.
     * @return String formateado.
     */
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

    private String processText(String text) {
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

    private String getString(String text, int i, int endBracket) {
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

    public String applyGradient(String text, Color start, Color end) {
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

    private String replacePlaceholders(Player player, String message) {
        if (message == null || message.isEmpty()) {
            return ""; // o un mensaje predeterminado
        }
        for (Map.Entry<String, String> entry : getStringStringMap(player).entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }

    private Map<String, String> getStringStringMap(Player player) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%prefix%", getPrefix());
        placeholders.put("%oline_player%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        placeholders.put("%max_player%", String.valueOf(Bukkit.getMaxPlayers()));

        if (player != null) {
            placeholders.put("%player%", safeString(player.getName()));
            placeholders.put("%display_name%", safeString(player.getDisplayName()));
            placeholders.put("%world%", safeString(player.getWorld().getName()));
            placeholders.put("%heal%", safeString(String.valueOf(player.getHealth())));
            placeholders.put("%food%", safeString(String.valueOf(player.getFoodLevel())));
            placeholders.put("%heal_bar%", safeString(createBar((int) Math.ceil(player.getHealth()), "❤", "&#b90000", "&#ff4d4d")));
            placeholders.put("%food_bar%", safeString(createBar(player.getFoodLevel(), "🍗", "&#995c00", "&#ff9933")));
            placeholders.put("%kills%", safeString(String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS))));
            placeholders.put("%deaths%", safeString(String.valueOf(player.getStatistic(Statistic.DEATHS))));
            placeholders.put("%ping%", safeString(getPing(player)));
            placeholders.put("%x%", safeString(String.format("%.0f", player.getLocation().getX())));
            placeholders.put("%y%", safeString(String.format("%.0f", player.getLocation().getY())));
            placeholders.put("%z%", safeString(String.format("%.0f", player.getLocation().getZ())));
            return placeholders;
        }
        return placeholders;
    }

    private String getPing(Player player) {
        int ping;
        try {
            ping = player.getPing();
        } catch (Exception e) {
            ping = 0;
        }

        return String.valueOf(ping);
    }

    private String safeString(String value) {
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

    public String formatTime(long seconds) {
        return FormatTime.formatTime(seconds, FormatTime.TimeFormatType.DIGITAL);
    }

    public String formatTime(long time, boolean fromMillis, FormatTime.TimeFormatType timeFormatType) {
        return FormatTime.formatTime(time, fromMillis, timeFormatType);
    }

    public String formatTime(long timeSeconds, FormatTime.TimeFormatType timeFormatType) {
        return FormatTime.formatTime(timeSeconds, timeFormatType);
    }

    /**
     * Convierte un string de tiempo personalizado a milisegundos.
     * Ejemplos: "1d10m", "5m;35s;4t", "1h 30m 20s", "500ms"
     *
     * @param timeString String con el tiempo a convertir.
     * @return Tiempo en milisegundos.
     * @throws NumberFormatException Si el formato es inválido.
     */
    public long parseTime(String timeString) throws NumberFormatException {
        long totalMillis = 0;
        StringBuilder numberBuffer = new StringBuilder();

        for (char c : timeString.replace(" ", "").toCharArray()) { // quitamos los espacios
            if (Character.isDigit(c)) {
                numberBuffer.append(c);
            } else {
                if (!numberBuffer.isEmpty()) {
                    long value = Long.parseLong(numberBuffer.toString());
                    switch (c) {
                        case 's' -> totalMillis += value * 1000L;
                        case 'm' -> totalMillis += value * 60_000L;
                        case 'h' -> totalMillis += value * 3_600_000L;
                        case 'd' -> totalMillis += value * 86_400_000L;
                        default -> throw new NumberFormatException("Unidad de tiempo no válida: '" + c + "'");
                    }
                    numberBuffer.setLength(0);
                } else {
                    throw new NumberFormatException("Formato de tiempo inválido: valor faltante antes de '" + c + "'");
                }
            }
        }

        if (!numberBuffer.isEmpty()) {
            throw new NumberFormatException("Formato de tiempo inválido: falta unidad para " + numberBuffer);
        }

        return totalMillis;
    }

    /**
     * Verifica si un jugador cumple con las condiciones de visualización.
     *
     * @param player El jugador a verificar.
     * @param condition La condición a evaluar.
     * @return true si el jugador cumple con la condición, false si no.
     */
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
                    String leftSide = parts[0].trim(); // La parte izquierda de la condición
                    String rightSide = parts[1].trim(); // La parte derecha de la condición

                    // Obtener los valores que estamos comparando
                    String leftValue = setPlaceholders(player, leftSide); // Esto obtiene el valor que corresponde a la parte izquierda
                    String rightValue = setPlaceholders(player, rightSide); // Esto obtiene el valor que corresponde a la parte izquierda

                    // Evaluar dependiendo del operador
                    try {
                        // Para números, convertir a enteros
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
                        // Si no es un número, compararlo como cadenas
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

    /**
     * Verifica si un texto contiene códigos de color tradicionales (&a, &f, etc.) o hexadecimales (&#ffffff, #ffffff).
     * @param text Texto a analizar.
     * @return true si hay códigos de color, false si no.
     */
    public boolean hasColorCodes(String text) {
        if (text == null) return false;

        String hexPattern = "(?i)(&#|#)[0-9a-f]{6}";
        String legacyPattern = "(?i)&[0-9a-fk-or]";

        return text.matches(".*" + hexPattern + ".*") || text.matches(".*" + legacyPattern + ".*");
    }

    public String removeColorCodes(String input) {
        if (input == null) return "";
        String cleaned = input.replaceAll("(?i)(&#|#)[0-9a-f]{6}", "");
        cleaned = cleaned.replaceAll("(?i)&[0-9a-fk-or]", "");
        return cleaned;
    }

    /**
     * Verifica si un texto cumple con una expresión regular y longitud mínima/máxima.
     *
     * @param input         Texto a validar.
     * @param regex         Expresión regular a usar (por defecto: solo letras, números, espacios, guion y guion bajo).
     * @param minLength     Longitud mínima permitida (por defecto: 3).
     * @param maxLength     Longitud máxima permitida (por defecto: 16).
     * @return true si el texto es válido, false si no.
     */
    public boolean isValidText(String input, String regex, Integer minLength, Integer maxLength) {
        if (input == null || input.isEmpty()) return false;
        // Valores por defecto
        String pattern = (regex != null) ? regex : "^[a-zA-Z0-9_\\- ]+$";
        int min = (minLength != null) ? minLength : 3;
        int max = (maxLength != null) ? maxLength : 16;

        // Validar longitud
        if (input.length() < min || input.length() > max) {
            return true;
        }

        // Validar regex
        return !input.matches(pattern);
    }

    /**
     * Retorna el texto si es válido según los parámetros, o el valor por defecto si es null/vacío,
     * o null si no pasa la validación.
     *
     * @param input         Texto a validar.
     * @param regex         Expresión regular a usar (por defecto: solo letras, números, espacios, guion y guion bajo).
     * @param minLength     Longitud mínima permitida (por defecto: 3).
     * @param maxLength     Longitud máxima permitida (por defecto: 16).
     * @return El texto válido o null si no cumple con la validación.
     */
    public String getValidatedText(String input, String regex, Integer minLength, Integer maxLength) {
        if (input == null || input.isEmpty()) return "";

        String pattern = (regex != null) ? regex : "^[a-zA-Z0-9_\\- ]+$";
        int min = (minLength != null) ? minLength : 3;
        int max = (maxLength != null) ? maxLength : 16;

        return (input.length() >= min && input.length() <= max && input.matches(pattern)) ? input : "";
    }

    public String getLocationString(Location location) {
        if (location == null) return "no data";
        return String.format("X: %.2f, Y: %.2f, Z: %.2f, World: %s", location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
    }
}

