package ex.nervisking;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PermissionCache {

    private final long cooldownMillis; // tiempo de refresco
    private final Map<UUID, CachedOp> opCache;
    private final Map<UUID, Map<String, CachedPermission>> permCache;

    // Constructor en segundos
    public PermissionCache(int cooldownSeconds) {
        this(cooldownSeconds * 1000L);
    }

    // Constructor en milisegundos
    public PermissionCache(long cooldownMillis) {
        this.cooldownMillis = cooldownMillis;
        this.opCache = new HashMap<>();
        this.permCache = new HashMap<>();

    }

    @Contract("_ -> new")
    public static @NotNull PermissionCache of(int cooldownSeconds) {
        return new PermissionCache(cooldownSeconds * 1000L);
    }

    @Contract("_ -> new")
    public static @NotNull PermissionCache of(long cooldownMillis) {
        return new PermissionCache(cooldownMillis);
    }

    // === OP CHECK ===
    public boolean isOp(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        CachedOp data = opCache.get(uuid);

        if (data == null || now - data.lastCheck() > cooldownMillis) {
            boolean value = player.isOp() || hasPermission(player, "owner");
            data = new CachedOp(value, now);
            opCache.put(uuid, data);
        }

        return data.value();
    }

    // === PERMISSION CHECK ===
    public boolean hasPermission(Player player, String permission) {
        return hasPermission(player, permission, false);
    }

    public boolean hasPermission(@NotNull Player player, String permission, boolean forceRefresh) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        permCache.putIfAbsent(uuid, new HashMap<>());
        Map<String, CachedPermission> perms = permCache.get(uuid);

       String perm = ExApi.getPlugin().getName().toLowerCase() + "." + permission;

        CachedPermission data = perms.get(perm);

        if (forceRefresh || data == null || now - data.lastCheck() > cooldownMillis) {
            boolean value = player.hasPermission(perm);
            data = new CachedPermission(value, now);
            perms.put(perm, data);
        }

        return data.value();
    }

    // === CHECK GENÉRICO ===
    public boolean check(Player player, @NotNull String permission) {
        if (permission.equalsIgnoreCase("owner")) {
            return isOp(player);
        }
        return hasPermission(player, permission);
    }

    // === PERMISOS MÚLTIPLES ===
    public boolean hasAnyPermission(Player player, String @NotNull ... permissions) {
        for (String perm : permissions) {
            if (check(player, perm)) return true;
        }
        return false;
    }

    // === LIMPIEZA MANUAL ===
    public void clear(Player player) {
        UUID uuid = player.getUniqueId();
        opCache.remove(uuid);
        permCache.remove(uuid);
    }

    // === LIMPIEZA DE ENTRADAS VIEJAS ===
    public void cleanupOldEntries() {
        long now = System.currentTimeMillis();

        opCache.entrySet().removeIf(e -> now - e.getValue().lastCheck > cooldownMillis * 10);
        permCache.entrySet().removeIf(e -> e.getValue().isEmpty());
    }

    // === CLASES INTERNAS ===
    private record CachedOp(boolean value, long lastCheck) { }
    private record CachedPermission(boolean value, long lastCheck) { }
}