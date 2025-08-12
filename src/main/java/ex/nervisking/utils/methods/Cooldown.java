package ex.nervisking.utils.methods;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Pattern.ToUse;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldown {

    private final Map<UUID, Long> cooldowns;

    public Cooldown() {
        this.cooldowns = new HashMap<>();
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Cooldown of() {
        return new Cooldown();
    }

    @ToUse(
            value = "Intenta activar una acción con cooldown para un jugador.",
            params = {
                    "uuid: UUID del jugador que ejecuta la acción",
                    "cooldown: Tiempo en milisegundos"
            },
            returns = "si el jugador está en cooldown, false si puede usar la acción"
    )
    public boolean tryCooldown(UUID uuid, long cooldown) {
        long currentTime = System.currentTimeMillis();

        if (isOnCooldown(uuid)){
            return true;
        }

        cooldowns.put(uuid, currentTime + cooldown);
        return false;
    }

    @ToUse(
            value = "Intenta activar una acción con cooldown para un jugador.",
            params = {
                    "uuid: UUID del Jugador que ejecuta la acción",
                    "cooldown: Tiempo en milisegundos"
            },
            returns = "true si el jugador está en cooldown, false si puede usar la acción"
    )
    public boolean tryCooldown(UUID uuid, String cooldown) {
        long currentTime = System.currentTimeMillis();

        if (isOnCooldown(uuid)) {
            return true;
        }

        long cooldownMillis = ExApi.getUtils().parseTime(cooldown);
        if (cooldownMillis <= 0) {
            return true; // o lanzar error/logger
        }

        cooldowns.put(uuid, currentTime + cooldownMillis);
        return false;
    }

    @ToUse(
            value = "Obtiene el tiempo restante del cooldown en milisegundos.",
            params = "uuid: UUID del Jugador",
            returns = "Tiempo restante en milisegundos (0 si no está en cooldown)"
    )
    public long getCooldownTime(UUID uuid) {
        Long endTime = cooldowns.get(uuid);
        if (endTime == null) return 0L;

        return Math.max(endTime - System.currentTimeMillis(), 0L);
    }

    @ToUse(
            value = "Verifica si el jugador está en cooldown.",
            params = "uuid: UUID del Jugador",
            returns = "true si está en cooldown, false si no"
    )
    public boolean isOnCooldown(UUID uuid) {
        Long endTime = cooldowns.get(uuid);
        return endTime != null && endTime > System.currentTimeMillis();
    }

    @ToUse(value = "Elimina el cooldown del jugador.", params = "uuid: UUID del Jugador")
    public void removeCooldown(UUID uuid) {
        cooldowns.remove(uuid);
    }

    @ToUse
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