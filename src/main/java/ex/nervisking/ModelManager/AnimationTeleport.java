package ex.nervisking.ModelManager;

public enum AnimationTeleport {

    SPIRAL(),
    CIRCLE(),
    DOUBLE_SPIRAL(),
    VERTICAL_PULSE(),
    FLOATING_CIRCLE();


    public static AnimationTeleport fromString(String name) {
        try {
            return AnimationTeleport.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
