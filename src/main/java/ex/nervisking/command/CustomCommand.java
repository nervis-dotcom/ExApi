package ex.nervisking.command;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Pattern.ToUse;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @since 1.0.0
 * Clase base para comandos personalizados.
 */
public abstract class CustomCommand extends UtilsManagers implements BaseCommand {

    private final String name;
    private final String description;
    private final String per;
    private final boolean permissionRequired;
    private final List<String> aliases;

    public CustomCommand() {
        CommandInfo info = this.getClass().getAnnotation(CommandInfo.class);
        if (info != null) {
            this.name = info.name();
            this.description = info.description();
            this.permissionRequired = info.permission();
            this.per = info.per();
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

    @Override
    public String per() {
        return per;
    }

    @ToUse(value = "Verificar si el sender tiene permiso para usar el comando")
    public boolean hasPermission(Sender sender) {
        return hasPermission(sender.getCommandSender(), "command." + getName());
    }

    @ToUse(value = "Verificar si el sender tiene ese permiso")
    public boolean hasPermission(Sender sender, String permission) {
        return hasPermission(sender.getCommandSender(), permission);
    }

    @ToUse(value = "Verificar si el sender tiene es permiso para usar el comando")
    public boolean hasSubPermission(Sender sender, String permission) {
        return hasPermission(sender.getCommandSender(), "command." + getName() + "." + permission);
    }

    @ToUse(value = "Permite enviarle mensaje al sender")
    public void sendMessage(Sender sender, String... message) {
        sender.sendMessage(message);
    }

    @ToUse(value = "Permite enviarle mensaje al sender")
    public void sendMessage(Sender sender, List<String> messages) {
        sender.sendMessage(messages);
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void noPermission(Sender sender) {
        sender.sendMessage(ExApi.getPermissionMessage());
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void neverConnected(Sender sender, String target) {
        sender.sendMessage(ExApi.getNeverConnected().replace("%player%", target));
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void noConsole(Sender sender) {
        sender.sendMessage(ExApi.getConsoleMessage());
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void invalidityAmount(Sender sender) {
        sender.sendMessage(ExApi.getInvalidityAmountMessage());
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void noOnline(Sender sender, String target) {
        sender.sendMessage(ExApi.getNoOnlineMessage().replace("%player%", target));
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void sendHelp(Sender sender, String... usages) {
        sender.sendMessage("%prefix% &eUso del comando:");
        for (String usage : usages) {
            sender.sendMessage("&7 - /" + getName() + " " + usage);
        }
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void help(Sender sender, String... args) {
        help(sender, args == null ? List.of() : Arrays.asList(args));
    }

    @ToUse(value = "Enviá un mensaje al sender")
    public void help(Sender sender, List<String> args) {
        sender.sendMessage(ExApi.getUsage().replace("%command%", getName()));
        if (args != null && !args.isEmpty()) {
            sender.sendMessage(" ");
            for (String arg : args) {
                sender.sendMessage(arg);
            }
            sender.sendMessage(" ");
        }

        if (sender.isOp() || hasOp(sender.getCommandSender())) {
            if (!getDescription().isEmpty()) {
                sender.sendMessage(ExApi.getDescription().replace("%description%", getDescription()));
            }
            if (!getAliases().isEmpty()) {
                sender.sendMessage(ExApi.getAliases().replace("%aliases%", String.join(", ", getAliases())));
            }
            if (getPermission()) {
                sender.sendMessage(ExApi.getPermissions().replace("%permission%",
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
        Arguments arg = Arguments.of(args);
        Completions completions = this.onTab(Sender.of(sender), arg, Completions.of());

        String spm = arg.get(arg.size() - 1);
        if (!arg.isEmpty() && spm != null) {
            completions.filter(s -> s.startsWith(spm.toLowerCase()));
        } else {
            completions.addPlayerOnline();
        }

        return completions.asList();
    }

    public abstract void onCommand(Sender sender, Arguments args);
    public Completions onTab(Sender sender, Arguments args, Completions completions) {
        return completions;
    }
}