package ex.nervisking.command;

import ex.nervisking.ExApi;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    public void noPermission(Sender sender) {
        sendMessage(sender.getCommandSender(), ExApi.getPermissionMessage());
    }

    public void help(Sender sender, String... args) {
        help(sender, args == null ? List.of() : Arrays.asList(args));
    }

    public void help(Sender sender, List<String> args) {
        sendMessage(sender.getCommandSender(), ExApi.getUsage().replace("%command%", getName()));

        if (args != null && !args.isEmpty()) {
            sendMessage(sender.getCommandSender(), " ");
            for (String arg : args) {
                sendMessage(sender.getCommandSender(), arg);
            }
            sendMessage(sender.getCommandSender(), " ");
        }

        if (sender.getCommandSender().isOp() || hasOp(sender.getCommandSender())) {
            if (!getDescription().isEmpty()) {
                sendMessage(sender.getCommandSender(), ExApi.getDescription().replace("%description%", getDescription()));
            }
            if (!getAliases().isEmpty()) {
                sendMessage(sender.getCommandSender(), ExApi.getAliases().replace("%aliases%", String.join(", ", getAliases())));
            }
            if (getPermission()) {
                sendMessage(sender.getCommandSender(), ExApi.getPermissions().replace("%permission%",
                        ExApi.getPlugin().getName().toLowerCase() + ".command." + getName()));
            }
        }
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        return this.onCommand(Sender.of(sender), Arguments.of(args));
    }

    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command cmd, @NotNull String alias, String @NotNull [] args) {
        return this.onTab(Sender.of(sender), Arguments.of(args), Completions.of());
    }

    public abstract boolean onCommand(Sender sender, Arguments args);
    public List<String> onTab(Sender sender, Arguments args, Completions completions) {
        return completions.asList();
    }
}