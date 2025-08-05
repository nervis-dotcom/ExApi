package ex.nervisking.ModelManager;

public enum Plugins {

    WORLDGUARD("WorldGuard"),
    PLACEHOLDERAPI("PlaceholderAPI"),
    VAULT("Vault"),
    VOICECHAT("voicechat"),
    PACKETEVENTS("packetevents"),
    LUCKPERMS("LuckPerms");

    private final String name;

    Plugins(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Plugins fromString(String name) {
        try {
            return Plugins.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}