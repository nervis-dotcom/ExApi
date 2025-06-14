package ex.nervisking.utils.TeleportAnimations;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.TeleportAnimation;
import ex.nervisking.ModelManager.TeleportParticle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VerticalPulseAnimation extends BukkitRunnable implements TeleportAnimation {

    private final Player player;
    private TeleportParticle particleEffect;
    private double pulseHeight = 3.0;
    private double step = 0.3;

    public VerticalPulseAnimation(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        }

        Location base = player.getLocation();
        for (double y = 0; y < pulseHeight; y += step) {
            Location loc = base.clone().add(0, y, 0);
            player.getWorld().spawnParticle(particleEffect.getParticle(), loc, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public void start(TeleportParticle particleEffect) {
        if (this.particleEffect != null) {
            cancel(); // Detener cualquier animación previa
        }
        this.particleEffect = particleEffect;
        runTaskTimer(ExApi.getPlugin(), 0L, 10L);
    }

    @Override
    public void stop() {
        cancel();
    }
}
