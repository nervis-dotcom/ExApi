package ex.nervisking.command;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Pattern.ToUse;
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

    private final CommandSender commandSender;
    private final UtilsManagers utilsManagers;

    public Sender(CommandSender commandSender) {
        this.commandSender = commandSender;
        this.utilsManagers = ExApi.getUtilsManagers();
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Sender of(CommandSender sender) {
        return new Sender(sender);
    }

    @ToUse(value = "Obtener el CommandSender")
    public CommandSender getCommandSender() {
        return commandSender;
    }

    @ToUse(value = "Verificar si el sender es jugador")
    public boolean isPlayer() {
        return commandSender instanceof Player;
    }

    @ToUse(value = "Verificar si el sender es la consola")
    public boolean isConsole() {
        return !isPlayer();
    }

    @ToUse(value = "Obtener el Player")
    public Player asPlayer() {
        return isPlayer() ? (Player) commandSender : null;
    }

    @ToUse(value = "Verificar si es un jugador")
    public void ifPlayer(Consumer<Player> action) {
        if (isPlayer()) {
            action.accept(asPlayer());
        }
    }

    @Deprecated(since = "1.0.2", forRemoval = true)
    public boolean equals(Player player) {
        return commandSender instanceof Player p && p.equals(player);
    }

    @ToUse(value = "Verificar el target es igual al player")
    public boolean parse(Player target) {
        return commandSender instanceof Player player && player.equals(target);
    }

    @ToUse(value = "Verificar si el player es op")
    public boolean isOp() {
        return commandSender.isOp() || utilsManagers.hasOp(commandSender);
    }

    @ToUse(value = "Obtener el nombre del jugador")
    public String getName() {
        return isPlayer() ? asPlayer().getName() : commandSender.getName();
    }

    @ToUse(value = "Obtener la ubicación del jugador")
    public Location getLocation() {
        return isPlayer() ? asPlayer().getLocation() : null;
    }

    @ToUse(value = "Obtener el inventario del jugador")
    public PlayerInventory getInventory() {
        return isPlayer() ? asPlayer().getInventory() : null;
    }

    @ToUse(value = "Obtener el mundo del jugador")
    public World getWorld() {
        return isPlayer() ? asPlayer().getWorld() : null;
    }

    @ToUse(value = "Obtener la uuid del jugador")
    public UUID getUniqueId() {
        return isPlayer() ? asPlayer().getUniqueId() : null;
    }

    @ToUse(value = "Permite enviarle mensaje al sender")
    public void sendMessage(String... messages) {
        utilsManagers.sendMessage(commandSender, messages);
    }

    @ToUse(value = "Permite enviarle mensaje al sender")
    public void sendMessage(List<String> messages) {
        utilsManagers.sendMessage(commandSender, messages);
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void noPermission() {
        utilsManagers.sendMessage(commandSender, ExApi.getPermissionMessage());
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void neverConnected(String target) {
        utilsManagers.sendMessage(commandSender, ExApi.getNeverConnected().replace("%player%", target));
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void noConsole() {
        utilsManagers.sendMessage(commandSender, ExApi.getConsoleMessage());
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void invalidityAmount() {
        utilsManagers.sendMessage(commandSender, ExApi.getInvalidityAmountMessage());
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void noOnline(String target) {
        utilsManagers.sendMessage(commandSender, ExApi.getNoOnlineMessage().replace("%player%", target));
    }
}