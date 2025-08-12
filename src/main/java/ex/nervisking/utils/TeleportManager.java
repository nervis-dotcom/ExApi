package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.*;
import ex.nervisking.ModelManager.Pattern.ToUse;
import ex.nervisking.utils.TeleportAnimations.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class TeleportManager {

    private final Player player;
    private final Location destination;
    private String message;
    private String messageInTeleport;
    private String messageSuccess;
    private String noDelayPermission; // permiso para no esperar delay
    private TeleportSound sound;
    private TeleportSound soundInTeleport;
    private TeleportParticle particle;
    private TeleportMessageError errorMessage;
    private final UtilsManagers utilsManagers;
    private int delayTicks;
    private Location initialLocation;
    private TeleportAnimation teleportAnimation;
    private static final Set<UUID> teleportingPlayers = new HashSet<>();

    public TeleportManager(Player player, Location destination) {
        this.player = player;
        this.destination = destination;
        this.utilsManagers = ExApi.getUtilsManagers();
        this.delayTicks = 0;
        this.noDelayPermission = null;
    }

    public TeleportManager(Player player, Location destination, @NotNull Consumer<TeleportManager> action) {
        this(player, destination);
        action.accept(this);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull TeleportManager of(Player player, Location destination) {
        return new TeleportManager(player, destination);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull TeleportManager of(Player player, Location destination, @NotNull Consumer<TeleportManager> action) {
        return new TeleportManager(player, destination, action);
    }

    @ToUse
    public static void andRun(Player player, Location destination, @NotNull Consumer<TeleportManager> action) {
        new TeleportManager(player, destination, action);
    }

    @ToUse(
            value = "Establece la animación de teletransporte.",
            params = "Animation: animation Tipo de animación.",
            returns = "Instancia de TeleportManager."
    )
    public TeleportManager setTeleportAnimation(AnimationTeleport animation) {
        if (animation == null) {
            this.teleportAnimation = null;
        } else {
            switch (animation) {
                case SPIRAL:
                    this.teleportAnimation = new SpiralAnimation(player);
                    break;
                case CIRCLE:
                    this.teleportAnimation = new ExpandingCircleAnimation(player);
                    break;
                case DOUBLE_SPIRAL:
                    this.teleportAnimation = new DoubleSpiralAnimation(player);
                    break;
                case VERTICAL_PULSE:
                    this.teleportAnimation = new VerticalPulseAnimation(player);
                    break;
                case FLOATING_CIRCLE:
                    this.teleportAnimation = new FloatingCircleAnimation(player);
                    break;
                default:
                    this.teleportAnimation = null;
            }
        }
        return this;
    }

    @ToUse
    public TeleportManager setMessage(String message) {
        this.message = message;
        return this;
    }

    @ToUse
    public TeleportManager setMessageInTeleport(String messageInTeleport, String messageSuccess) {
        this.messageInTeleport = messageInTeleport;
        this.messageSuccess = messageSuccess;
        return this;
    }

    @ToUse
    public TeleportManager setSound(TeleportSound sound) {
        this.sound = sound;
        return this;
    }

    @ToUse
    public TeleportManager setSoundInTeleport(TeleportSound soundInTeleport) {
        this.soundInTeleport = soundInTeleport;
        return this;
    }

    @ToUse
    public TeleportManager setParticle(TeleportParticle particle) {
        this.particle = particle;
        return this;
    }

    @ToUse
    public TeleportManager setDelayTicks(int ticks) {
        this.delayTicks = ticks * 20; // Convert seconds to ticks (1 second = 20 ticks)
        return this;
    }

    @ToUse
    public TeleportManager setNoDelayPermission(String permiso) {
        this.noDelayPermission = permiso;
        return this;
    }

    @ToUse
    public boolean teleport() {
        if (!isSafeLocation(destination)) {
            errorMessage = TeleportMessageError.UNSAFE;
            return false;
        }

        boolean result = player.teleport(destination);

        if (!result) {
            errorMessage = TeleportMessageError.BUKKIT;
            return false;
        }

        if (message != null) {
            utilsManagers.sendMessage(player, message);
        }
        if (sound != null) {
            player.playSound(player.getLocation(), sound.getSound(), 1.0f, 1.0f);
        }
        if (particle != null) {
            player.getWorld().spawnParticle(particle.getParticle(), player.getLocation(), 30);
        }

        return true;
    }

    @ToUse
    public void teleportAsync(Runnable onCancel) {
        this.teleportAsync(null, onCancel);
    }

    @ToUse
    public void teleportAsync(Runnable onSuccess, Runnable onCancel) {
        if (teleportingPlayers.contains(player.getUniqueId())) {
            errorMessage = TeleportMessageError.IN_PROGRESS;
            onCancel.run();
            return;
        }

        if (!isSafeLocation(destination)) {
            errorMessage = TeleportMessageError.UNSAFE;
            onCancel.run();
            return;
        }

        teleportingPlayers.add(player.getUniqueId());
        // Si el permiso está configurado y el jugador lo tiene, teletransporte inmediato
        if (noDelayPermission != null && utilsManagers.hasPermission(player, noDelayPermission)) {
            boolean result = teleport();
            teleportingPlayers.remove(player.getUniqueId());

            if (!result) {
                onCancel.run();
                return;
            }
            if (onSuccess != null) {
                onSuccess.run();
            }
            return;
        }

        initialLocation = player.getLocation().clone();

        if (teleportAnimation != null) {
            teleportAnimation.start(particle);
        }
        Scheduler.runTimer(task -> {
            if (player.isDead() || !player.isOnline()) {
                teleportingPlayers.remove(player.getUniqueId()); // <- limpiar
                if (teleportAnimation != null) {
                    teleportAnimation.stop();
                }
                task.cancel();
                errorMessage = TeleportMessageError.NULL_PLAYER;
                onCancel.run();
                return;
            }

            if (playerMoved(player.getLocation(), initialLocation)) {
                teleportingPlayers.remove(player.getUniqueId()); // <- limpiar
                if (teleportAnimation != null) {
                    teleportAnimation.stop();
                }
                task.cancel();
                errorMessage = TeleportMessageError.MOVE;
                onCancel.run();
                return;
            }

            if (soundInTeleport != null && delayTicks % 20 == 0 && delayTicks > 0) {
                player.playSound(player.getLocation(), soundInTeleport.getSound(), 1.0f, 1.0f);
            }

            if (messageInTeleport != null && delayTicks % 20 == 0 && delayTicks > 0) {
                int secondsLeft = delayTicks / 20;
                String formattedMessage = messageInTeleport.replace("%time%", String.valueOf(secondsLeft));
                utilsManagers.sendActionBar(player, formattedMessage);
            }

            if (delayTicks-- <= 0) {
                if (messageInTeleport != null) {
                    utilsManagers.sendActionBar(player, messageSuccess != null ? messageSuccess : " ");
                }

                teleportingPlayers.remove(player.getUniqueId()); // <- limpiar
                if (teleportAnimation != null) {
                    teleportAnimation.stop();
                }

                task.cancel();
                boolean result = player.teleport(destination);
                if (!result) {
                    errorMessage = TeleportMessageError.ERROR;
                    onCancel.run();
                    return;
                }

                if (sound != null){
                    player.playSound(player.getLocation(), sound.getSound(), 1.0f, 1.0f);
                }
                if (particle != null) {
                    player.getWorld().spawnParticle(particle.getParticle(), player.getLocation(), 30);
                }
                if (message != null) {
                    utilsManagers.sendMessage(player, message);
                }
                if (onSuccess != null) {
                    onSuccess.run();
                }
            }
        }, 0L, 1L);
    }

    private boolean playerMoved(Location to, Location from) {
        return from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
    }

    @ToUse
    public String getErrorMessage() {
        return errorMessage.getMessage();
    }

    @ToUse
    public void setErrorMessage(TeleportMessageError messageError, String message) {
        TeleportMessageError.setMessage(messageError, message);
    }

    // help
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

        return (feetBlock.getType() == Material.AIR || feetBlock.isPassable()) && (headBlock.getType() == Material.AIR || headBlock.isPassable());
    }

    public enum TeleportMessageError {

        UNSAFE("No se puede teletransportar: el destino no es seguro."),
        BUKKIT("No se pudo teletransportar al jugador (fallo interno de Bukkit)."),
        IN_PROGRESS("Ya estás en proceso de teletransporte."),
        NULL_PLAYER("El jugador ya no está disponible."),
        MOVE("Teletransporte cancelado por moverse."),
        ERROR("No se pudo teletransportar al jugador.");

        private String message;

        TeleportMessageError(String defaultMessage) {
            this.message = defaultMessage;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String newMessage) {
            this.message = newMessage;
        }

        public static void setMessage(TeleportMessageError error, String newMessage) {
            if (error != null && newMessage != null) {
                error.setMessage(newMessage);
            }
        }
    }
}