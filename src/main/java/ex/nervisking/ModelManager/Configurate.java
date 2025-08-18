package ex.nervisking.ModelManager;

import ex.nervisking.ModelManager.Pattern.KeyLet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Configurate(@KeyLet String fileName, @KeyLet String folderName) {

    public Configurate(@KeyLet String fileNam) {
        this(fileNam, null);
    }

    @Contract("_, _ -> new")
    public static @NotNull Configurate of(@KeyLet String fileName, @KeyLet String folderName) {
        return new Configurate(fileName, folderName);
    }

    @Contract("_ -> new")
    public static @NotNull Configurate of(@KeyLet String fileName) {
        return new Configurate(fileName);
    }
}