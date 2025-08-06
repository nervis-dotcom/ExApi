package ex.nervisking.utils;

import ex.nervisking.ExApi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExLog {

    private static final File logsFolder = new File(ExApi.getPlugin().getDataFolder(), "logs");
    private final static UtilsManagers utilsManagers = ExApi.getUtilsManagers();
    private final static String PREFIX = utilsManagers.getPrefix();

    public static void sendInfo(String... messages) {
        send(List.of(messages), "&a ");
    }

    public static void sendInfo(List<String> messages) {
        send(messages, "&a ");
    }

    public static void sendWarning(String... messages) {
        send(List.of(messages), "&e ");
    }

    public static void sendWarning(List<String> messages) {
        send(messages, "&e ");
    }

    public static void sendError(String... messages) {
        send(List.of(messages), "&4 ");
    }

    public static void sendError(List<String> messages) {
        send(messages, "&4 ");
    }

    public static void sendDebug(String... messages) {
        send(List.of(messages), "&b ");
    }

    public static void sendDebug(List<String> messages) {
        send(messages, "&b ");
    }

    public static void logToFile(String message) {
        if (!logsFolder.exists()) logsFolder.mkdirs();

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        File logFile = new File(logsFolder, date + ".log");

        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write("[" + date + " " + LocalTime.now().withNano(0) + "] " + utilsManagers.removeColorCodes(message) + "\n");
        } catch (IOException e) {
            sendError("No se pudo escribir en el archivo de log: " + e.getMessage());
        }
    }

    public static void sendException(Throwable throwable) {
        sendError("Se produjo un error: " + throwable.getMessage());
        for (StackTraceElement element : throwable.getStackTrace()) {
            sendDebug("  at " + element.toString());
        }
    }

    public static void sendInfo(String tag, List<String> messages) {
        send(messages, "&a [" + tag + "] ");
    }

    public static void sendInfo(String tag, String... messages) {
        send(List.of(messages), "&a [" + tag + "] ");
    }

    private static void send(List<String> messages, String prefix) {
        if (messages == null || messages.isEmpty()) return;
        if (messages.size() == 1) {
            utilsManagers.sendConsoleMessage(PREFIX + prefix + messages.getFirst());
            return;
        }

        utilsManagers.sendConsoleMessage(PREFIX);
        for (String message : messages) {
            utilsManagers.sendConsoleMessage(prefix + message);
        }
    }
}