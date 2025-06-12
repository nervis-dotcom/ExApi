package ex.nervisking.utils.methods;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownMulti {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public boolean tryCooldown(UUID uuid, String actionId, long cooldownMillis) {
        long currentTime = System.currentTimeMillis();
        if (isOnCooldown(uuid, actionId)) return true;

        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(actionId, currentTime + cooldownMillis);
        return false;
    }

    public boolean tryCooldown(UUID uuid, String actionId, String cooldown) {
        long currentTime = System.currentTimeMillis();
        if (isOnCooldown(uuid, actionId)) return true;

        long cooldownMillis = parseTime(cooldown);
        if (cooldownMillis <= 0) return true; // o lanzar error/logger

        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(actionId, currentTime + cooldownMillis);
        return false;
    }

    public long getCooldownTime(UUID uuid, String actionId) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) return 0L;

        Long endTime = playerCooldowns.get(actionId);
        if (endTime == null) return 0L;

        return Math.max(endTime - System.currentTimeMillis(), 0L);
    }

    public boolean isOnCooldown(UUID uuid, String actionId) {
        return getCooldownTime(uuid, actionId) > 0;
    }

    public void removeCooldown(UUID uuid, String actionId) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns != null) playerCooldowns.remove(actionId);
    }

    public void removeAllCooldowns(UUID uuid) {
        cooldowns.remove(uuid);
    }

    public String getCooldownTimeFormat(UUID uuid, String actionId) {
        long seconds = getCooldownTime(uuid, actionId) / 1000;
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

    private long parseTime(String timeString) {
        long totalMillis = 0;
        boolean valid = false;

        String[] timeUnits = timeString.split(" ");
        for (String unit : timeUnits) {
            try {
                long l = Long.parseLong(unit.substring(0, unit.length() - 1));
                if (unit.endsWith("d")) totalMillis += l * 86400000L;
                else if (unit.endsWith("h")) totalMillis += l * 3600000L;
                else if (unit.endsWith("m")) totalMillis += l * 60000L;
                else if (unit.endsWith("s")) totalMillis += l * 1000L;
                else continue; // unidad inválida

                valid = true;
            } catch (NumberFormatException ignored) {}
        }

        return valid ? totalMillis : -1;
    }

}
