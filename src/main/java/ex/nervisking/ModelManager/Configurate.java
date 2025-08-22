package ex.nervisking.ModelManager;

import ex.nervisking.ModelManager.Pattern.KeyAlphaNum;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Configurate(@KeyAlphaNum String fileName, @KeyAlphaNum String folderName) {

    public Configurate(@KeyAlphaNum String fileNam) {
        this(fileNam, null);
    }

    @Contract("_, _ -> new")
    public static @NotNull Configurate of(@KeyAlphaNum String fileName, @KeyAlphaNum String folderName) {
        return new Configurate(fileName, folderName);
    }

    @Contract("_ -> new")
    public static @NotNull Configurate of(@KeyAlphaNum String fileName) {
        return new Configurate(fileName);
    }
}