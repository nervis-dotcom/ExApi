package ex.nervisking.ModelManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public record Coordinate(String world, double x, double y, double z, float yaw, float pitch) {

    public static Coordinate of(String world, double x, double y, double z, float yaw, float pitch) {
        return new Coordinate(world, x, y, z, yaw, pitch);
    }
    public static Coordinate of(String world, double x, double y, double z) {
        return new Coordinate(world, x, y, z, 0, 0);
    }

    public Location getLocation() {
        String worldName = world;
        if (worldName == null || worldName.isEmpty()) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(world, x, y, z, yaw, pitch);
    }
}