package ex.nervisking.ModelManager;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Configurate(String fileName, String folderName) {

    public Configurate(String fileNam) {
        this(fileNam, null);
    }

    @Contract("_, _ -> new")
    public static @NotNull Configurate of(String fileName, String folderName) {
        return new Configurate(fileName, folderName);
    }

    @Contract("_ -> new")
    public static @NotNull Configurate of(String fileName) {
        return new Configurate(fileName);
    }
}