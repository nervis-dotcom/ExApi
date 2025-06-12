package ex.nervisking.utils;

import ex.nervisking.ModelManager.DateFormatType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String getNowFormatted(DateFormatType formatType) {
        return getNowFormatted(formatType, null);
    }

    public static String getNowFormatted(DateFormatType formatType, String customPattern) {
        DateTimeFormatter formatter;

        if (formatType == DateFormatType.CUSTOM) {
            if (customPattern == null || customPattern.isEmpty()) {
                throw new IllegalArgumentException("Se requiere un patrón personalizado para CUSTOM.");
            }
            formatter = DateTimeFormatter.ofPattern(customPattern);
        } else {
            formatter = DateTimeFormatter.ofPattern(formatType.getPattern());
        }

        return LocalDateTime.now().format(formatter);
    }

    public static LocalDateTime parse(String dateString, DateFormatType formatType) {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(formatType.getPattern()));
    }

    public static LocalDateTime parse(String dateString, String customPattern) {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(customPattern));
    }
}
