package ex.nervisking.utils.methods;

import ex.nervisking.ExApi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldown {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    /**
     * Intenta activar una acción con cooldown para un jugador.
     * @param uuid del Jugador que ejecuta la acción
     * @param cooldown Tiempo en milisegundos
     * @return true si el jugador está en cooldown, false si puede usar la acción
     */
    public boolean tryCooldown(UUID uuid, long cooldown) {
        long currentTime = System.currentTimeMillis();

        if (isOnCooldown(uuid)) return true;

        cooldowns.put(uuid, currentTime + cooldown);
        return false;
    }

    /**
     * Intenta activar una acción con cooldown para un jugador.
     * @param uuid del Jugador que ejecuta la acción
     * @param cooldown Tiempo en milisegundos
     * @return true si el jugador está en cooldown, false si puede usar la acción
     */
    public boolean tryCooldown(UUID uuid, String cooldown) {
        long currentTime = System.currentTimeMillis();

        if (isOnCooldown(uuid)) return true;

        long cooldownMillis = ExApi.getUtils().parseTime(cooldown);
        if (cooldownMillis <= 0) return true; // o lanzar error/logger

        cooldowns.put(uuid, currentTime + cooldownMillis);
        return false;
    }


    /**
     * Obtiene el tiempo restante del cooldown en milisegundos.
     * @param uuid del Jugador
     * @return Tiempo restante en milisegundos (0 si no está en cooldown)
     */
    public long getCooldownTime(UUID uuid) {
        Long endTime = cooldowns.get(uuid);
        if (endTime == null) return 0L;

        return Math.max(endTime - System.currentTimeMillis(), 0L);
    }

    /**
     * Verifica si el jugador está en cooldown.
     * @param uuid del Jugador
     * @return true si está en cooldown, false si no
     */
    public boolean isOnCooldown(UUID uuid) {
        Long endTime = cooldowns.get(uuid);
        return endTime != null && endTime > System.currentTimeMillis();
    }

    /**
     * Elimina el cooldown del jugador.
     * @param uuid del Jugador
     */
    public void removeCooldown(UUID uuid) {
        cooldowns.remove(uuid);
    }

    public String getCooldownTimeFormat(UUID uuid) {
        long seconds = getCooldownTime(uuid) / 1000;
        long days = seconds / 86400;
        seconds %= 86400;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append("s");

        return sb.toString().trim();
    }

}
