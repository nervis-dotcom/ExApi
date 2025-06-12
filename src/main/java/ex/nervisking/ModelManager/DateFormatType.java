package ex.nervisking.ModelManager;

public enum DateFormatType {

    DATE("yyyy-MM-dd"),
    TIME("HH:mm:ss"),
    DATE_TIME("yyyy-MM-dd HH:mm:ss"),
    FILE_SAFE("yyyy-MM-dd_HH-mm-ss"),
    ISO("yyyy-MM-dd'T'HH:mm:ss"),
    CUSTOM(""); // Para formato personalizado

    private final String pattern;

    DateFormatType(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
