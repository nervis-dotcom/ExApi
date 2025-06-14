package ex.nervisking.ModelManager;

import java.awt.Color;

public class ColorUtil {

    public static Color parse(String input) {
        if (input == null) return Color.WHITE;

        if (input.startsWith("&") && input.length() == 2) {
            CustomColor custom = CustomColor.fromCode(input);
            return custom != null ? custom.getColor() : Color.WHITE;
        }

        if (input.startsWith("#") && input.length() == 7) {
            try {
                return Color.decode(input);
            } catch (NumberFormatException e) {
                return Color.WHITE;
            }
        }

        return Color.WHITE;
    }
}
