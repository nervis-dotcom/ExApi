package ex.nervisking.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

/**
 * @since 1.0.0
 * Clase que encapsula los argumentos de un comando y provee métodos
 * convenientes para acceder y convertir dichos argumentos.
 */
public class Arguments {

    private final String[] args;

    /**
     * Constructor que recibe el arreglo original de argumentos.
     *
     * @param args Array de argumentos recibido del comando.
     */
    public Arguments(String[] args) {
        this.args = args;
    }

    /**
     * @since 1.1.0
     */
    public static Arguments of(String[] args) {
       return new Arguments(args);
    }

    /**
     * @since 1.1.0
     */
    public static Arguments of() {
       return new Arguments(new String[0]);
    }

    /**
     * Obtiene la cantidad total de argumentos.
     *
     * @return Número total de argumentos.
     */
    public int size() {
        return args.length;
    }

    /**
     * Verifica si existe un argumento en el índice dado.
     *
     * @param index Índice a verificar.
     * @return true si existe argumento en ese índice, false en caso contrario.
     */
    public boolean has(int index) {
        return !isEmpty() && args.length == index;
    }

    private boolean h(int index) {
        return index >= 0 && index < args.length;
    }

    /**
     * Obtiene el argumento en el índice indicado como String.
     *
     * @param index Índice del argumento.
     * @return El argumento en forma de String, o null si no existe.
     */
    public String get(int index) {
        return h(index) ? args[index] : null;
    }

    /**
     * Obtiene el argumento en el índice indicado como String, o un valor por defecto si no existe.
     *
     * @param index Índice del argumento.
     * @param def Valor por defecto si no existe el argumento.
     * @return El argumento en forma de String, o el valor por defecto.
     */
    public String getOrDefault(int index, String def) {
        return h(index) ? args[index] : def;
    }

    /**
     * Convierte el argumento en el índice dado a minúsculas.
     *
     * @param index Índice del argumento.
     * @return El argumento en minúsculas, o null si no existe.
     */
    public String toLowerCase(int index) {
        String value = get(index);
        return value != null ? value.toLowerCase() : null;
    }

    /**
     * Convierte el argumento en el índice dado a mayúsculas.
     *
     * @param index Índice del argumento.
     * @return El argumento en mayúsculas, o null si no existe.
     */
    public String toUpperCase(int index) {
        String value = get(index);
        return value != null ? value.toUpperCase() : null;
    }

    /**
     * Intenta convertir el argumento en el índice dado a entero.
     *
     * @param index Índice del argumento.
     * @param def Valor por defecto si no se puede convertir.
     * @return El valor entero del argumento o el valor por defecto.
     */
    public int getInt(int index, int def) {
        try {
            return Integer.parseInt(get(index));
        } catch (Exception e) {
            return def;
        }
    }

    public int getInt(int index) throws NumberFormatException{
        return Integer.parseInt(get(index));
    }

    /**
     * Intenta convertir el argumento en el índice dado a double.
     *
     * @param index Índice del argumento.
     * @param def Valor por defecto si no se puede convertir.
     * @return El valor double del argumento o el valor por defecto.
     */
    public double getDouble(int index, double def) {
        try {
            return Double.parseDouble(get(index));
        } catch (Exception e) {
            return def;
        }
    }

    public double getDouble(int index) throws NumberFormatException{
        return Double.parseDouble(get(index));
    }

    /**
     * Intenta convertir el argumento en el índice dado a double.
     *
     * @param index Índice del argumento.
     * @param def Valor por defecto si no se puede convertir.
     * @return El valor long del argumento o el valor por defecto.
     */
    public long getLong(int index, long def) {
        try {
            return Long.parseLong(get(index));
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Intenta convertir el argumento en el índice dado a double.
     *
     * @param index Índice del argumento.
     * @param def Valor por defecto si no se puede convertir.
     * @return El valor float del argumento o el valor por defecto.
     */
    public float getFloat(int index, float def) {
        try {
            return Float.parseFloat(get(index)); // ← corrección aquí
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Intenta convertir el argumento en el índice dado a booleano.
     *
     * @param index Índice del argumento.
     * @param def Valor por defecto si no se puede convertir.
     * @return El valor booleano del argumento o el valor por defecto.
     */
    public boolean getBoolean(int index, boolean def) {
        try {
            return Boolean.parseBoolean(get(index));
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Une y concatena todos los argumentos desde el índice especificado
     * separados por espacios.
     *
     * @param startIndex Índice desde donde comenzar a unir argumentos.
     * @return Cadena con los argumentos unidos por espacios.
     */
    public String join(int startIndex) {
        if (startIndex >= args.length) return ""; // prevenir IllegalArgumentException
        return String.join(" ", Arrays.copyOfRange(args, startIndex, args.length));
    }

    public String join(int start, int end) {
        if (start >= end || start >= args.length) return "";
        return String.join(" ", Arrays.copyOfRange(args, start, Math.min(end, args.length)));
    }

    /**
     * Obtiene el arreglo original de argumentos.
     *
     * @return Arreglo original de argumentos.
     */
    public String[] getRaw() {
        return args;
    }

    /**
     * Verifica si el argumento en el índice indicado es un entero válido.
     *
     * @param index Índice del argumento.
     * @return true si es un entero válido, false en caso contrario.
     */
    public boolean isInt(int index) {
        try {
            Integer.parseInt(get(index));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el argumento en el índice indicado es un entero válido.
     *
     * @param index Índice del argumento.
     * @return true si es un entero válido, false en caso contrario.
     */
    public boolean isLong(int index) {
        try {
            Long.parseLong(get(index));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el argumento en el índice indicado es un número decimal válido.
     *
     * @param index Índice del argumento.
     * @return true si es un número decimal válido, false en caso contrario.
     */
    public boolean isDouble(int index) {
        try {
            Double.parseDouble(get(index));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el argumento en el índice indicado es un booleano válido ("true" o "false").
     *
     * @param index Índice del argumento.
     * @return true si es un booleano válido, false en caso contrario.
     */
    public boolean isBoolean(int index) {
        String value = get(index);
        return value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"));
    }

    /**
     * Intenta obtener un jugador online por nombre exacto del argumento en el índice dado.
     *
     * @param index Índice del argumento con el nombre del jugador.
     * @return Jugador online si existe, o null si no está online o no existe.
     */
    public Player getOnlinePlayer(int index) {
        String name = get(index);
        return name != null ? Bukkit.getPlayerExact(name) : null;
    }

    /**
     * Intenta obtener un jugador offline por nombre exacto del argumento en el índice dado.
     *
     * @param index Índice del argumento con el nombre del jugador.
     * @return Jugador offline correspondiente o null si no existe.
     */
    public OfflinePlayer getOfflinePlayer(int index) {
        String name = get(index);
        return name != null ? Bukkit.getOfflinePlayer(name) : null;
    }

    /**
     * Intenta convertir el argumento en el índice dado a un UUID.
     *
     * @param index Índice del argumento.
     * @return UUID si la conversión es exitosa, o null en caso contrario.
     */
    public UUID getUUID(int index) {
        try {
            return UUID.fromString(get(index));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verifica si no hay argumentos.
     *
     * @return true si no hay argumentos, false si hay alguno.
     */
    public boolean isEmpty() {
        return args.length == 0;
    }

    /**
     * Verifica si hay menos argumentos que el mínimo requerido.
     *
     * @param minCount cantidad mínima requerida
     * @return true si hay menos argumentos que minCount
     */
    public boolean lacksMinArgs(int minCount) {
        return size() < minCount;
    }

    /**
     * Verifica si hay más argumentos que el máximo permitido.
     *
     * @param maxCount cantidad máxima permitida
     * @return true si hay más argumentos que maxCount
     */
    public boolean lacksMaxArgs(int maxCount) {
        return size() > maxCount;
    }

    /**
     * Verifica si la cantidad de argumentos es igual o mayor al mínimo requerido.
     *
     * @param required la cantidad mínima de argumentos necesarios
     * @return true si hay al menos {@code required} argumentos, false en caso contrario
     */
    public boolean hasMaxArgs(int required) {
        return args.length >= required;
    }

    /**
     * Verifica si la cantidad de argumentos es igual o mayor al mínimo requerido.
     *
     * @param required la cantidad mínima de argumentos necesarios
     * @return true si hay al menos {@code required} argumentos, false en caso contrario
     */
    public boolean hasMinArgs(int required) {
        return args.length <= required;
    }

    /**
     * Devuelve el último argumento si existe, o null si no hay argumentos.
     *
     * @return el último argumento como String, o null si no hay argumentos
     */
    public String getLast() {
        if (isEmpty()) {
            return null;  // No hay argumentos
        }
        return get(size() - 1); // Devuelve el último argumento
    }

    /**
     * Verifica si el argumento en el índice dado coincide con alguno de los valores esperados (ignora mayúsculas/minúsculas).
     *
     * @param index Índice del argumento.
     * @param values Valores permitidos a comparar.
     * @return true si coincide con alguno, false en caso contrario.
     */
    public boolean equalsIgnoreCase(int index, String... values) {
        String value = get(index);
        if (value == null) return false;

        for (String expected : values) {
            if (value.equalsIgnoreCase(expected)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si el argumento en el índice dado coincide con alguno de los valores esperados.
     *
     * @param index Índice del argumento.
     * @param values Valores permitidos a comparar.
     * @return true si coincide con alguno, false en caso contrario.
     */
    public boolean equals(int index, String... values) {
        String value = get(index);
        if (value == null) return false;

        for (String expected : values) {
            if (value.equals(expected)) {
                return true;
            }
        }
        return false;
    }
}