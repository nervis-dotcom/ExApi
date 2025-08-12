package ex.nervisking.utils;

public class FormatTime {

    public enum TimeFormatType {

        DIGITAL,       // Ej: 01:02:03, 1 día 01:02:03
        TEXTUAL,       // Ej: 1 día, 2 horas, 3 minutos, 4 segundos
        SHORT_TEXTUAL; // Ej: 1d 2h 3m 4s

        public String build(long days, long hours, long minutes, long seconds) {
            return switch (this) {
                case DIGITAL -> buildDigital(days, hours, minutes, seconds);
                case TEXTUAL -> buildTextual(days, hours, minutes, seconds);
                case SHORT_TEXTUAL -> buildShortTextual(days, hours, minutes, seconds);
            };
        }

        private String buildDigital(long days, long hours, long minutes, long seconds) {
            if (days > 0) {
                return String.format("%d %s %02d:%02d:%02d", days, (days == 1 ? "día" : "días"), hours, minutes, seconds);
            } else if (hours > 0) {
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            } else if (minutes > 0) {
                return String.format("%02d:%02d", minutes, seconds);
            } else {
                return String.format("%d %s", seconds, (seconds == 1 ? "segundo" : "segundos"));
            }
        }

        private String buildTextual(long days, long hours, long minutes, long seconds) {
            StringBuilder sb = new StringBuilder();
            if (days > 0) sb.append(days).append(" ").append(days == 1 ? "día" : "días").append(", ");
            if (hours > 0) sb.append(hours).append(" ").append(hours == 1 ? "hora" : "horas").append(", ");
            if (minutes > 0) sb.append(minutes).append(" ").append(minutes == 1 ? "minuto" : "minutos").append(", ");
            if (seconds > 0 || sb.length() == 0) sb.append(seconds).append(" ").append(seconds == 1 ? "segundo" : "segundos");

            // Eliminar coma final si existe
            String result = sb.toString().trim();
            if (result.endsWith(",")) result = result.substring(0, result.length() - 1);
            return result;
        }

        private String buildShortTextual(long days, long hours, long minutes, long seconds) {
            StringBuilder sb = new StringBuilder();
            if (days > 0) sb.append(days).append("d ");
            if (hours > 0) sb.append(hours).append("h ");
            if (minutes > 0) sb.append(minutes).append("m ");
            if (seconds > 0 || sb.length() == 0) sb.append(seconds).append("s");
            return sb.toString().trim();
        }
    }

    // ---------- MÉTODOS PRINCIPALES ----------

    /**
     * Formatea un tiempo a string con un formato específico.
     *
     * @param time Tiempo en milisegundos o segundos según fromMillis.
     * @param fromMillis True si time está en milisegundos, false si en segundos.
     * @param type Tipo de formato a usar.
     * @return String con el tiempo formateado.
     * @since 1.1.0
     */
    public static String formatTime(long time, boolean fromMillis, TimeFormatType type) {
        return formatTime(fromMillis ? Math.max(0L, time) / 1000L : time, type);
    }

    /**
     * Formatea un tiempo en segundos a string según tipo de formato.
     *
     * @param seconds Tiempo en segundos.
     * @param type Tipo de formato a usar.
     * @return String con el tiempo formateado.
     * @since 1.1.0
     */
    public static String formatTime(long seconds, TimeFormatType type) {
        if (seconds < 0) return "00:00";
        long days = seconds / 86400;
        seconds %= 86400;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        return type.build(days, hours, minutes, seconds);
    }

    // ---------- MÉTODOS OPCIONALES (Formato DIGITAL por defecto) ----------

    /**
     * Formatea un tiempo en milisegundos o segundos a formato DIGITAL.
     *
     * @param time Tiempo en milisegundos o segundos según fromMillis.
     * @param fromMillis True si time está en milisegundos, false si en segundos.
     * @return String con el tiempo formateado en formato DIGITAL.
     * @since 1.1.0
     */
    public static String formatTime(long time, boolean fromMillis) {
        return formatTime(time, fromMillis, TimeFormatType.DIGITAL);
    }

    /**
     * Formatea un tiempo en segundos a formato DIGITAL.
     *
     * @param seconds Tiempo en segundos.
     * @return String con el tiempo formateado en formato DIGITAL.
     * @since 1.1.0
     */
    public static String formatTime(long seconds) {
        return formatTime(seconds, TimeFormatType.DIGITAL);
    }
}