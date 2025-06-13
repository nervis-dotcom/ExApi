package ex.nervisking.ModelManager;

import org.bukkit.Sound;

public enum TeleportSound {
    PORTAL(Sound.ENTITY_ENDERMAN_TELEPORT),
    LEVEL_UP(Sound.ENTITY_PLAYER_LEVELUP),
    FIREWORK(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH);

    private final Sound sound;

    TeleportSound(Sound sound) {
        this.sound = sound;
    }

    public Sound getSound() {
        return sound;
    }

    public static TeleportSound fromString(String name) {
        try {
            return TeleportSound.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
