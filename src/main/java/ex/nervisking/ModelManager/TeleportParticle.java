package ex.nervisking.ModelManager;

import org.bukkit.Particle;

public enum TeleportParticle {

    PORTAL(Particle.PORTAL),
    FLAME(Particle.FLAME),
    ENCHANT(Particle.ENCHANT),
    SMOKE(Particle.SMOKE),
    CLOUD(Particle.CLOUD),
    DRAGON_BREATH(Particle.DRAGON_BREATH),
    REDSTONE(Particle.DUST),
    HEART(Particle.HEART),
    VILLAGER_HAPPY(Particle.HAPPY_VILLAGER),
    SNOWFLAKE(Particle.SNOWFLAKE),;

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
