package ex.nervisking.Placeholder;

import ex.nervisking.utils.UtilsManagers;

public class PlaceholderExample extends UtilsManagers {

    public void asb() {
        Placeholder.registerPlaceholder("heal", (player, params) -> player != null ? String.valueOf(player.getHealth()) : "0");
        Placeholder.registerPlaceholder("name", (player, params) -> params.length > 0 ? params[0] : "Sin nombre");
        Placeholder.registerPlaceholder("hex", (player, params) -> params.length >= 3 ? "Texto: " + params[0] + " - " + params[1] + " a " + params[2] : "Formato inválido");
        Placeholder.registerPlaceholder("format", (player, params) -> {
            if (player == null) {
                return "player no folder";  // Si el jugador es nulo, retorna un mensaje de error
            }

            String string = params.length > 0 ? params[0] : "";  // Obtener el primer parámetro (por ejemplo, el texto)

            try {
                // Reemplaza los placeholders y trata de parsear el número
                double amount = Double.parseDouble(setPlaceholders(player, string.replace("{", "%").replace("}", "%")));
                return format(amount);  // Formatea el número y lo retorna
            } catch (NumberFormatException e) {
                return "Invalid amount";  // Si no se puede parsear el número, retorna un error
            }
        });

        String message = "Vida: %heal%, Nombre: %name_Steve%, Color: %hex_Hola_#ff0000_#0000ff%";

    }
}
