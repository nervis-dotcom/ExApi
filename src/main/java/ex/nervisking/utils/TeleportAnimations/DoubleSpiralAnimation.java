package ex.nervisking.utils.TeleportAnimations;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.TeleportAnimation;
import ex.nervisking.ModelManager.TeleportParticle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DoubleSpiralAnimation extends BukkitRunnable implements TeleportAnimation {

    private final Player player;
    private TeleportParticle particleEffect;

    private double angle = 0;
    private double height = 0;
    private final double maxHeight = 2;
    private final double step = 0.1;
    private final double radius = 1.2;

    public DoubleSpiralAnimation(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        }

        Location base = player.getLocation();

        for (int i = 0; i < 2; i++) {
            double dir = (i == 0) ? 1 : -1;
            double x = Math.cos(angle * dir) * radius;
            double z = Math.sin(angle * dir) * radius;
            Location loc = base.clone().add(x, height, z);
            player.getWorld().spawnParticle(particleEffect.getParticle(), loc, 1, 0, 0, 0, 0);
        }

        angle += Math.PI / 10;
        height += step;

        if (height > maxHeight) {
            height = 0;
            angle = 0;
        }
    }

    @Override
    public void start(TeleportParticle particleEffect) {
        if (this.particleEffect != null) {
            cancel(); // Detener cualquier animación previa
        }
        this.particleEffect = particleEffect;
        runTaskTimer(ExApi.getPlugin(), 0L, 1L);
    }

    @Override
    public void stop() {
        cancel();
    }
}
