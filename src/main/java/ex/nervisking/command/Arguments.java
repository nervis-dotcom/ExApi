package ex.nervisking.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

/**
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
        return index >= 0 && index < args.length;
    }

    /**
     * Obtiene el argumento en el índice indicado como String.
     *
     * @param index Índice del argumento.
     * @return El argumento en forma de String, o null si no existe.
     */
    public String get(int index) {
        return has(index) ? args[index] : null;
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
        return String.join(" ", Arrays.copyOfRange(args, startIndex, args.length));
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
     * Verifica si la cantidad de argumentos es igual o mayor al mínimo requerido.
     *
     * @param required la cantidad mínima de argumentos necesarios
     * @return true si hay al menos {@code required} argumentos, false en caso contrario
     */
    public boolean hasMinimumArgs(int required) {
        return args.length >= required;
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
}
