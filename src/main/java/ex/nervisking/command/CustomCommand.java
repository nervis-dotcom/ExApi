package ex.nervisking.command;

import ex.nervisking.ExApi;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @since 1.0.0
 * Clase base para comandos personalizados.
 */
public abstract class CustomCommand extends UtilsManagers implements BaseCommand {

    private final String name;
    private final String description;
    private final boolean permissionRequired;
    private final List<String> aliases;

    public CustomCommand() {
        CommandInfo info = this.getClass().getAnnotation(CommandInfo.class);
        if (info != null) {
            this.name = info.name();
            this.description = info.description();
            this.permissionRequired = info.permission();
            this.aliases = Arrays.asList(info.aliases());
        } else {
            throw new IllegalStateException("Falta @CommandInfo en: " + getClass().getName());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean getPermission() {
        return permissionRequired;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    public boolean hasPermission(Sender sender) {
        return hasPermission(sender.getCommandSender(), "command." + getName());
    }

    public boolean hasPermission(Sender sender, String permission) {
        return hasPermission(sender.getCommandSender(), permission);
    }

    public boolean hasSubPermission(Sender sender, String subPermission) {
        return hasPermission(sender.getCommandSender(), "command." + getName() + "." + subPermission);
    }

    public void sendMessage(Sender sender, String message) {
        this.sendMessage(sender.getCommandSender(), message);
    }

    public void sendMessage(Sender sender, String... message) {
        this.sendMessage(sender.getCommandSender(), message);
    }

    public void sendMessage(Sender sender, List<String> messages) {
        this.sendMessage(sender.getCommandSender(), messages);
    }

    // Ayudas y mensajes
    public void noPermission(Sender sender) {
        sendMessage(sender, ExApi.getPermissionMessage());
    }

    public void neverConnected(Sender sender, String target) {
        sendMessage(sender, ExApi.getNeverConnected().replace("%player%", target));
    }

    public void noConsole(Sender sender) {
        sendMessage(sender, ExApi.getConsoleMessage());
    }

    public void invalidityAmount(Sender sender) {
        sendMessage(sender, ExApi.getInvalidityAmountMessage());
    }

    public void noOnline(Sender sender, String target) {
        sendMessage(sender, ExApi.getNoOnlineMessage().replace("%player%", target));
    }

    public void sendHelp(Sender sender, String... usages) {
        sendMessage(sender, "%prefix% &eUso del comando:");
        for (String usage : usages) {
            sendMessage(sender, "&7 - /" + getName() + " " + usage);
        }
    }

    public void help(Sender sender, String... args) {
        help(sender, args == null ? List.of() : Arrays.asList(args));
    }

    public void help(Sender sender, List<String> args) {
        sendMessage(sender, ExApi.getUsage().replace("%command%", getName()));

        if (args != null && !args.isEmpty()) {
            sendMessage(sender, " ");
            for (String arg : args) {
                sendMessage(sender, arg);
            }
            sendMessage(sender, " ");
        }

        if (sender.isOp() || hasOp(sender.getCommandSender())) {
            if (!getDescription().isEmpty()) {
                sendMessage(sender, ExApi.getDescription().replace("%description%", getDescription()));
            }
            if (!getAliases().isEmpty()) {
                sendMessage(sender, ExApi.getAliases().replace("%aliases%", String.join(", ", getAliases())));
            }
            if (getPermission()) {
                sendMessage(sender, ExApi.getPermissions().replace("%permission%",
                        ExApi.getPlugin().getName().toLowerCase() + ".command." + getName()));
            }
        }
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        this.onCommand(Sender.of(sender), Arguments.of(args));
        return true;
    }

    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String alias, @NotNull String @NotNull [] args) {
        Arguments arguments = Arguments.of(args);
        Completions completions = this.onTab(Sender.of(sender), arguments, Completions.of());

        if (!arguments.isEmpty()) {
            String lastArg = arguments.get(arguments.size() - 1);
            completions.filter(s -> s.startsWith(lastArg.toLowerCase()));
        } else {
            completions.add(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }

        return completions.asList();
    }

    public abstract void onCommand(Sender sender, Arguments args);
    public Completions onTab(Sender sender, Arguments args, Completions completions) {
        return completions;
    }
}