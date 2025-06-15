package ex.nervisking.ModelManager;

import org.bukkit.Sound;

public enum TeleportSound {

    PORTAL(Sound.ENTITY_ENDERMAN_TELEPORT),
    LEVEL_UP(Sound.ENTITY_PLAYER_LEVELUP),
    FIREWORK(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH),
    ANVIL(Sound.BLOCK_ANVIL_USE),
    BELL(Sound.BLOCK_BELL_USE),
    NOTE_BLOCK(Sound.BLOCK_NOTE_BLOCK_PLING),
    CHEST_OPEN(Sound.BLOCK_CHEST_OPEN),
    CHEST_CLOSE(Sound.BLOCK_CHEST_CLOSE),
    ENDER_CHEST_OPEN(Sound.BLOCK_ENDER_CHEST_OPEN),
    ENDER_CHEST_CLOSE(Sound.BLOCK_ENDER_CHEST_CLOSE),
    PORTAL_TRAVEL(Sound.BLOCK_PORTAL_TRAVEL),
    PORTAL_TRIGGER(Sound.BLOCK_PORTAL_TRIGGER),
    PORTAL_AMBIENT(Sound.BLOCK_PORTAL_AMBIENT);

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
