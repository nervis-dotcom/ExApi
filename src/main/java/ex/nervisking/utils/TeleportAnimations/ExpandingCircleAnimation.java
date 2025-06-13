package ex.nervisking.utils.TeleportAnimations;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.TeleportAnimation;
import ex.nervisking.ModelManager.TeleportParticle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class ExpandingCircleAnimation extends BukkitRunnable implements TeleportAnimation {

    private final Player player;
    private TeleportParticle particleEffect;
    private double radius = 0.1;
    private boolean expanding = true;
    private final double maxRadius = 3.0;
    private final double step = 0.1;

    public ExpandingCircleAnimation(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            this.cancel();
            return;
        }

        Location baseLoc = player.getLocation().clone();
        int points = 20;

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Location particleLoc = baseLoc.clone().add(x, 0, z);
            player.getWorld().spawnParticle(particleEffect.getParticle(), particleLoc, 1, 0, 0, 0, 0);
        }

        if (expanding) {
            radius += step;
            if (radius >= maxRadius) {
                expanding = false;
            }
        } else {
            radius -= step;
            if (radius <= 0.1) {
                expanding = true;
            }
        }
    }

    @Override
    public void start(TeleportParticle particleEffect) {
        if (this.particleEffect != null) {
            this.cancel(); // Stop any previous animation
        }
        this.particleEffect = particleEffect;
        this.runTaskTimer(ExApi.getPlugin(), 0L, 3L);
    }

    @Override
    public void stop() {
        this.cancel();
    }
}
