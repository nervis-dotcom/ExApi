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

    // Configurables
    private final double maxHeight = 2.0;
    private final double verticalStep = 0.10;
    private final long resetEveryTicks = (long) (maxHeight / verticalStep); // ticks hasta alcanzar altura

    private long ticksElapsed = 0;

    public SpiralAnimation(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            this.cancel();
            return;
        }

        Location baseLoc = player.getLocation().add(0, 0, 0);

        double x = Math.cos(angle);
        double z = Math.sin(angle);

        Location particleLoc = baseLoc.clone().add(x, height, z);
        player.getWorld().spawnParticle(particleEffect.getParticle(), particleLoc, 1, 0, 0, 0, 0);

        angle += Math.PI / 8;
        height += verticalStep;
        ticksElapsed++;

        // Reinicia la espiral cada vez que alcanza la altura máxima
        if (ticksElapsed >= resetEveryTicks) {
            angle = 0;
            height = 0;
            ticksElapsed = 0;
        }
    }

    @Override
    public void start(TeleportParticle particleEffect) {
        if (this.particleEffect != null) {
            this.cancel();
        }
        this.particleEffect = particleEffect;
        this.runTaskTimer(ExApi.getPlugin(), 0L, 1L); // Cada tick
    }

    @Override
    public void stop() {
        this.cancel();
    }
}
