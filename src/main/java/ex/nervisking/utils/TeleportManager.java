package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.AnimationTeleport;
import ex.nervisking.ModelManager.TeleportAnimation;
import ex.nervisking.ModelManager.TeleportParticle;
import ex.nervisking.ModelManager.TeleportSound;
import ex.nervisking.utils.TeleportAnimations.ExpandingCircleAnimation;
import ex.nervisking.utils.TeleportAnimations.SpiralAnimation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeleportManager {

    private final Player player;
    private final Location destination;
    private String message;
    private String messageInTeleport;
    private String noDelayPermission = null; // permiso para no esperar delay
    private TeleportSound sound;
    private TeleportSound soundInTeleport;
    private TeleportParticle particle;
    private String errorMessage;
    private final UtilsManagers utilsManagers;
    private int delayTicks = 0;
    private Location initialLocation;
    private static final Set<UUID> teleportingPlayers = new HashSet<>();
    private TeleportAnimation teleportAnimation;

    public TeleportManager setTeleportAnimation(AnimationTeleport animation) {
        if (animation == null) {
            this.teleportAnimation = null;
        } else if (animation == AnimationTeleport.SPIRAL) {
            this.teleportAnimation = new SpiralAnimation(player);
        } else if (animation == AnimationTeleport.CIRCLE) {
            this.teleportAnimation = new ExpandingCircleAnimation(player);
        }
        return this;
    }

    public TeleportManager(Player player, Location destination) {
        this.player = player;
        this.destination = destination;
        this.utilsManagers = ExApi.getUtilsManagers();
    }

    public TeleportManager setMessage(String message) {
        this.message = message;
        return this;
    }

    public TeleportManager setMessageInTeleport(String messageInTeleport) {
        this.messageInTeleport = messageInTeleport;
        return this;
    }

    public TeleportManager setSound(TeleportSound sound) {
        this.sound = sound;
        return this;
    }

    public TeleportManager setSoundInTeleport(TeleportSound soundInTeleport) {
        this.soundInTeleport = soundInTeleport;
        return this;
    }

    public TeleportManager setParticle(TeleportParticle particle) {
        this.particle = particle;
        return this;
    }

    public TeleportManager setDelayTicks(int ticks) {
        this.delayTicks = ticks * 20; // Convert seconds to ticks (1 second = 20 ticks)
        return this;
    }
    public TeleportManager setNoDelayPermission(String permiso) {
        this.noDelayPermission = permiso;
        return this;
    }

    public boolean teleport() {
        if (!isSafeLocation(destination)) {
            errorMessage = "No se puede teletransportar: el destino no es seguro.";
            return false;
        }

        boolean result = player.teleport(destination);

        if (!result) {
            errorMessage = "No se pudo teletransportar al jugador (fallo interno de Bukkit).";
            return false;
        }

        if (message != null) utilsManagers.sendMessage(player, message);
        if (sound != null) player.playSound(player.getLocation(), sound.getSound(), 1.0f, 1.0f);
        if (particle != null) player.getWorld().spawnParticle(particle.getParticle(), player.getLocation(), 30);

        return true;
    }

    public void teleportAsync(Runnable onSuccess, Runnable onCancel) {
        if (teleportingPlayers.contains(player.getUniqueId())) {
            errorMessage = "Ya estás en proceso de teletransporte.";
            onCancel.run();
            return;
        }

        if (!isSafeLocation(destination)) {
            errorMessage = "No se puede teletransportar: el destino no es seguro.";
            onCancel.run();
            return;
        }

        // Si el permiso está configurado y el jugador lo tiene, teletransporte inmediato
        if (noDelayPermission != null && utilsManagers.hasPermission(player, noDelayPermission)) {
            teleportingPlayers.add(player.getUniqueId());
            boolean result = teleport();
            teleportingPlayers.remove(player.getUniqueId());

            if (!result) {
                onCancel.run();
                return;
            }

            if (message != null) utilsManagers.sendMessage(player, message);
            if (sound != null) player.playSound(player.getLocation(), sound.getSound(), 1.0f, 1.0f);
            if (particle != null) player.getWorld().spawnParticle(particle.getParticle(), player.getLocation(), 30);

            onSuccess.run();
            return;
        }

        teleportingPlayers.add(player.getUniqueId()); // <- Agregar aquí
        initialLocation = player.getLocation().clone();

        if (teleportAnimation != null) {
            teleportAnimation.start(particle);
        }

        new BukkitRunnable() {
            int ticksLeft = delayTicks;

            @Override
            public void run() {

                if (player.isDead() || !player.isOnline()) {
                    teleportingPlayers.remove(player.getUniqueId()); // <- limpiar
                    if (teleportAnimation != null) teleportAnimation.stop();
                    cancel();
                    errorMessage = "El jugador ya no está disponible.";
                    onCancel.run();
                    return;
                }

                if (playerMoved(player, initialLocation)) {
                    teleportingPlayers.remove(player.getUniqueId()); // <- limpiar
                    if (teleportAnimation != null) teleportAnimation.stop();
                    cancel();
                    errorMessage = "Teletransporte cancelado por moverse.";
                    onCancel.run();
                    return;
                }

                if (soundInTeleport != null && ticksLeft % 20 == 0 && ticksLeft > 0) {
                    player.playSound(player.getLocation(), soundInTeleport.getSound(), 1.0f, 1.0f);
                }

                if (messageInTeleport != null && ticksLeft % 20 == 0 && ticksLeft > 0) {
                    int secondsLeft = ticksLeft / 20;
                    String formattedMessage = messageInTeleport.replace("%time%", String.valueOf(secondsLeft));
                    utilsManagers.actionBar(player, formattedMessage);
                }

                if (ticksLeft-- <= 0) {
                    if (messageInTeleport != null) {
                        utilsManagers.actionBar(player, "&aTeletransportando...");
                    }

                    teleportingPlayers.remove(player.getUniqueId()); // <- limpiar
                    if (teleportAnimation != null) teleportAnimation.stop();
                    cancel();

                    boolean result = player.teleport(destination);
                    if (!result) {
                        errorMessage = "No se pudo teletransportar al jugador.";
                        onCancel.run();
                        return;
                    }

                    if (sound != null) player.playSound(player.getLocation(), sound.getSound(), 1.0f, 1.0f);
                    if (particle != null) player.getWorld().spawnParticle(particle.getParticle(), player.getLocation(), 30);
                    if (message != null) utilsManagers.sendMessage(player, message);
                    onSuccess.run();
                }
            }
        }.runTaskTimer(ExApi.getPlugin(), 0L, 1L);
    }

    private boolean playerMoved(Player p, Location from) {
        Location to = p.getLocation();
        return from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private boolean isSafeLocation(Location location) {
        if (!hasPlayerSpace(location)) return false;
        if (isLavaNearby(location)) return false;

        if (!isInTheAir(location)) return true;

        Location safeLanding = findSafeLanding(location);
        if (safeLanding != null) {
            destination.setX(safeLanding.getX());
            destination.setY(safeLanding.getY());
            destination.setZ(safeLanding.getZ());
            return true;
        }

        return false;
    }

    private Location findSafeLanding(Location origin) {
        World world = origin.getWorld();
        int startY = origin.getBlockY();
        int minY = world.getMinHeight();
        int maxDepth = 50;

        for (int y = startY; y >= minY && (startY - y) <= maxDepth; y--) {
            Location checkLoc = new Location(world, origin.getX(), y, origin.getZ());
            Block feetBlock = checkLoc.getBlock();
            Block headBlock = checkLoc.clone().add(0, 1, 0).getBlock();
            Block groundBlock = checkLoc.clone().subtract(0, 1, 0).getBlock();

            boolean isGroundSolid = groundBlock.getType().isSolid();
            boolean isSpaceFree = feetBlock.getType() == Material.AIR && headBlock.getType() == Material.AIR;

            if (isGroundSolid && isSpaceFree && !isCaveRoofed(checkLoc)) {
                return checkLoc.add(0.5, 0, 0.5);
            }
        }

        return null;
    }

    private boolean isCaveRoofed(Location loc) {
        World world = loc.getWorld();
        int skyY = world.getMaxHeight();

        int solidCount = 0;
        for (int y = loc.getBlockY() + 2; y <= skyY; y++) {
            Block block = world.getBlockAt(loc.getBlockX(), y, loc.getBlockZ());
            if (block.getType().isSolid()) {
                solidCount++;
                if (solidCount >= 8) return true;
            }
        }

        return false;
    }

    private boolean isInTheAir(Location location) {
        Block blockBelow = location.clone().subtract(0, 1, 0).getBlock();
        return blockBelow.getType() == Material.AIR || blockBelow.getType() == Material.WATER;
    }

    private boolean isLavaNearby(Location location) {
        World world = location.getWorld();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = world.getBlockAt(location.clone().add(x, y, z));
                    Material type = block.getType();
                    if (type == Material.LAVA || type == Material.FIRE || type == Material.MAGMA_BLOCK) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasPlayerSpace(Location location) {
        Block feetBlock = location.getBlock();
        Block headBlock = location.clone().add(0, 1, 0).getBlock();

        return (feetBlock.getType() == Material.AIR || feetBlock.isPassable()) &&
                (headBlock.getType() == Material.AIR || headBlock.isPassable());
    }
}
