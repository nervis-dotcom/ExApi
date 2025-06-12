package ex.nervisking.utils.methods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeParser {

    /**
     * Convierte un string de tiempo personalizado a ticks (1 tick = 50ms).
     * Soporta:
     *  - t: ticks
     *  - ms: milisegundos
     *  - s: segundos
     *  - m: minutos
     *  - h: horas
     *  - d: días
     * Se puede combinar usando espacios, puntos y comas, o concatenado:
     * Ej: "1d10m", "5m;35s;4t", "1h 30m 20s"
     *
     * @param input tiempo en string
     * @return tiempo total en ticks
     * @throws IllegalArgumentException si no se reconoce el formato
     */
    public static long parseToTicks(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input no puede ser vacío");
        }

        // Normalizar separadores a espacios para dividir
        String normalized = input.toLowerCase().replace(";", " ").replace(",", " ").replace(".", " ");

        // Patrón para cada segmento: número + unidad
        Pattern pattern = Pattern.compile("(\\d+)(d|h|m{1,2}|s|t)?");
        // ms tiene dos letras, por eso m{1,2} cubre m y ms

        long totalTicks = 0;

        // Dividir en segmentos
        String[] parts = normalized.split("\\s+");

        for (String part : parts) {
            if (part.isEmpty()) continue;

            Matcher matcher = pattern.matcher(part);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Formato inválido en: " + part);
            }

            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            if (unit == null) {
                // Si no hay unidad, asumimos ticks
                unit = "t";
            }

            switch (unit) {
                case "t":
                    totalTicks += value;
                    break;
                case "ms":
                    totalTicks += value / 50;
                    break;
                case "m":
                    // minutos -> segundos -> ticks
                    totalTicks += value * 60 * 20;
                    break;
                case "s":
                    totalTicks += value * 20;
                    break;
                case "h":
                    totalTicks += value * 60 * 60 * 20;
                    break;
                case "d":
                    totalTicks += value * 24 * 60 * 60 * 20;
                    break;
                default:
                    throw new IllegalArgumentException("Unidad no soportada: " + unit);
            }
        }

        return totalTicks;
    }
}
