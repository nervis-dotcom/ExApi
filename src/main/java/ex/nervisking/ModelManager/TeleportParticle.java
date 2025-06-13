package ex.nervisking.ModelManager;

import org.bukkit.Particle;

public enum TeleportParticle {

    PORTAL(Particle.PORTAL),
    FLAME(Particle.FLAME),
    ENCHANT(Particle.ENCHANT);

    private final Particle particle;

    TeleportParticle(Particle particle) {
        this.particle = particle;
    }

    public Particle getParticle() {
        return particle;
    }

    public static TeleportParticle fromString(String name) {
        try {
            return TeleportParticle.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
