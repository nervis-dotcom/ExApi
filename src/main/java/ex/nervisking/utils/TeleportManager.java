package ex.nervisking.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.function.Consumer;

public class TeleportManager {

    // Comprueba si la ubicación es segura para teletransportarse
    public boolean isSafeLocation(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.AIR || block.getType() != Material.LIGHT) {
            return false;
        }
        if (isLavaNearby(location)) {
            return false;
        }

        return !isInTheAir(location);
    }

    // Comprobar si hay lava en la ubicación de destino o cerca
    private boolean isLavaNearby(Location location) {
        World world = location.getWorld();

        // Verificar bloques cercanos (por ejemplo, dentro de un radio de 2 bloques)
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    Block block = world.getBlockAt(location.clone().add(x, y, z));
                    if (block.getType() == Material.LAVA) {
                        return true;  // Hay lava en la zona
                    }
                }
            }
        }

        return false;
    }

    // Comprobar si el jugador está en el aire (sin bloques debajo)
    private boolean isInTheAir(Location location) {
        World world = location.getWorld();
        Block blockBelow = world.getBlockAt(location.clone().subtract(0, 1, 0));  // Bloque debajo de la ubicación

        // Si el bloque debajo es AIR o un bloque que no sostiene al jugador, está en el aire
        return blockBelow.getType() == Material.AIR || blockBelow.getType() == Material.WATER;
    }

    /** Metodo principal para realizar el teletransporte
     *
     * @param player jugador
     * @param destination destino a donde se teletransporta
     * @param callback resultado
     */
    public void sedTeleport(Player player, Location destination, Consumer<Boolean> callback) {
        if (isSafeLocation(destination)) {
            player.teleport(destination);
            callback.accept(true);
        } else {
            callback.accept(false);
        }
    }
}
