package ex.nervisking.utils.TeleportAnimations;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.TeleportAnimation;
import ex.nervisking.ModelManager.TeleportParticle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FloatingCircleAnimation extends BukkitRunnable implements TeleportAnimation {

    private final Player player;
    private TeleportParticle particleEffect;

    private double currentHeight = 0;
    private boolean ascending = true;

    private final double maxHeight = 2.0; // Altura máxima del círculo
    private final double step = 0.10; // Paso vertical entre cada punto del círculo
    private final double radius = 1; // Radio del círculo
    private final int points = 20; // Número de puntos en el círculo

    public FloatingCircleAnimation(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        }

        Location base = player.getLocation();

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Location loc = base.clone().add(x, currentHeight, z);
            player.getWorld().spawnParticle(particleEffect.getParticle(), loc, 1, 0, 0, 0, 0);
        }

        // Movimiento vertical
        if (ascending) {
            currentHeight += step;
            if (currentHeight >= maxHeight) ascending = false;
        } else {
            currentHeight -= step;
            if (currentHeight <= 0) ascending = true;
        }
    }

    @Override
    public void start(TeleportParticle particleEffect) {
        this.particleEffect = particleEffect;
        runTaskTimer(ExApi.getPlugin(), 0L, 1L);
    }

    @Override
    public void stop() {
        cancel();
    }
}
