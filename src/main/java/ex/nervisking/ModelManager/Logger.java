package ex.nervisking.ModelManager;

/**
 * Enum que representa los distintos niveles de logs usados para mensajes en consola.
 * <p>
 * Cada tipo tiene un color asociado para facilitar la identificación visual en la consola:
 * <ul>
 *   <li>INFO: Mensajes informativos (color verde claro)</li>
 *   <li>WARNING: Advertencias (color amarillo)</li>
 *   <li>ERROR: Errores graves (color rojo)</li>
 *   <li>DEBUG: Mensajes de depuración (color azul claro)</li>
 * </ul>
 *
 * El metodo {@link #getName()} devuelve el código de color para usar en el mensaje.
 * El metodo {@link #fromString(String)} permite obtener el enum a partir de un texto ignorando mayúsculas/minúsculas.
 */
public enum Logger {

    INFO("&a "),
    WARNING("&e "),
    ERROR("&4 "),
    DEBUG("&b ");

    private final String name;

    Logger(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Logger fromString(String name) {
        try {
            return Logger.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return INFO;
        }
    }
}