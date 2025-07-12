package ex.nervisking.command;

import ex.nervisking.ExApi;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @since 1.1.0
 */
public class Sender {

    private final UtilsManagers utilsManagers;
    private final CommandSender commandSender;

    public Sender(CommandSender commandSender) {
        this.commandSender = commandSender;
        this.utilsManagers = ExApi.getUtilsManagers();
    }

    public static Sender of(CommandSender sender) {
        return new Sender(sender);
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public boolean isPlayer() {
        return commandSender instanceof Player;
    }

    public boolean isConsole() {
        return !isPlayer();
    }

    public Player asPlayer() {
        return isPlayer() ? (Player) commandSender : null;
    }

    public void ifPlayer(Consumer<Player> action) {
        if (isPlayer()) {
            action.accept(asPlayer());
        }
    }

    public boolean isOp() {
        return commandSender.isOp();
    }

    public String getName() {
        return commandSender.getName();
    }

    public UUID getUniqueId() {
        return isPlayer() ? asPlayer().getUniqueId() : null;
    }

    public void sendMessage(String message) {
        utilsManagers.sendMessage(commandSender, message);
    }

    public void sendMessages(String... messages) {
        utilsManagers.sendMessage(commandSender, messages);
    }

    public void sendMessage(List<String> messages) {
        utilsManagers.sendMessage(commandSender, messages);
    }

    public boolean hasPermission(String permission) {
        return utilsManagers.hasPermission(commandSender, permission);
    }
}