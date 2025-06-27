package ex.nervisking.utils;

import ex.nervisking.ExApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Fake {

    public static void setDamage(Player player, double damage, String name) {
        double totalHealth = player.getHealth() + player.getAbsorptionAmount();
        double healthFinal = totalHealth - damage;

        if (healthFinal <= 0.0) {
            Cow entity = createEntity(player.getLocation().add(2,10,2), name);
            player.damage(damage, entity);
            Bukkit.getScheduler().runTaskLater(ExApi.getPlugin(), entity::remove, 1L);
        } else {
            player.damage(damage);
        }
    }

    private static Cow createEntity(Location loc, String name) {
        Cow entity = (Cow) loc.getWorld().spawnEntity(loc, EntityType.COW);
        entity.setCustomName(name);
        entity.setCustomNameVisible(false);
        entity.setSilent(true);
        entity.setInvulnerable(true);
        entity.setAI(false);
        entity.setCollidable(false);
        entity.setGravity(false);
        entity.setInvisible(true);
        return entity;
    }
}