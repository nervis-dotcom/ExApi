package ex.nervisking.utils.TeleportAnimations;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.TeleportAnimation;
import ex.nervisking.ModelManager.TeleportParticle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class SpiralAnimation extends BukkitRunnable implements TeleportAnimation {

    private final Player player;
    private TeleportParticle particleEffect;
    private double angle = 0;
    private double height = 0;
    private final  double maxHeight = 3;
    private final double radius = 1.0;

    public SpiralAnimation(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            this.cancel();
            return;
        }

        Location baseLoc = player.getLocation().add(0, 1, 0);

        double x = radius * Math.cos(angle);
        double z = radius * Math.sin(angle);
        Location particleLoc = baseLoc.clone().add(x, height, z);

        player.getWorld().spawnParticle(particleEffect.getParticle(), particleLoc, 1, 0, 0, 0, 0);

        angle += Math.PI / 8;
        height += 0.05;

        if (height > maxHeight) {
            height = 0;
            angle = 0;
        }
    }


    @Override
    public void start(TeleportParticle particleEffect) {
        if (this.particleEffect != null) {
            this.cancel(); // Stop any previous animation
        }
        this.particleEffect = particleEffect;
        this.runTaskTimer(ExApi.getPlugin(), 0L, 2L);
    }

    @Override
    public void stop() {
        this.cancel();
    }
}
