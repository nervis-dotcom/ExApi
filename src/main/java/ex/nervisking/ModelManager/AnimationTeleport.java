package ex.nervisking.ModelManager;

public enum AnimationTeleport {

    SPIRAL(),
    CIRCLE();

    public static AnimationTeleport fromString(String name) {
        try {
            return AnimationTeleport.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
