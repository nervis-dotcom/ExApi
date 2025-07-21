package ex.nervisking.command;

import ex.nervisking.ExApi;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Sender of(CommandSender sender) {
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

    public boolean equals(Player player) {
        return commandSender instanceof Player p && p.equals(player);
    }

    public boolean isOp() {
        return commandSender.isOp();
    }

    public String getName() {
        return isPlayer() ? asPlayer().getName() : commandSender.getName();
    }

    public Location getLocation() {
        return isPlayer() ? asPlayer().getLocation() : null;
    }

    public PlayerInventory getInventory() {
        return isPlayer() ? asPlayer().getInventory() : null;
    }

    public World getWorld() {
        return isPlayer() ? asPlayer().getWorld() : null;
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

    public boolean hasSubPermission(String permission) {
        return utilsManagers.hasPermission(commandSender, "command." + getName() + "." + permission);
    }
}