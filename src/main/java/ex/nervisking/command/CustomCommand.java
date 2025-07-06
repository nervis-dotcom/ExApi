package ex.nervisking.command;

import ex.nervisking.ExApi;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Deprecated(since = "1.0.0")
public abstract class CustomCommand extends UtilsManagers implements BaseCommand {

    private final Map<String, String> permission;

    @Deprecated(since = "1.0.0")
    public CustomCommand() {
        this.permission = new HashMap<>();
    }

    @Deprecated(since = "1.0.0")
    public abstract String getName();

    @Deprecated(since = "1.0.0")
    public abstract String getDescription();

    @Deprecated(since = "1.0.0")
    public abstract boolean getPermission();

    @Deprecated(since = "1.0.0")
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Deprecated(since = "1.0.0")
    public Map<String, String> getPermissions() {
        return permission;
    }

    @Deprecated(since = "1.0.0")
    public void addPermission(String description, String permission) {
        this.permission.put(description, permission);
    }

    @Deprecated(since = "1.0.0")
    public boolean hasPermission(CommandSender sender) {
        return hasPermission(sender, "command." + getName());
    }

    @Deprecated(since = "1.0.0")
    public boolean hasSubPermission(CommandSender sender, String subPermission) {
        return hasPermission(sender, "command." + getName() + "." + subPermission);
    }

    public void noPermission(CommandSender sender) {
        sendMessage(sender, ExApi.getPermissionMessage());
    }

    @Deprecated(since = "1.0.0")
    public void neverConnected(CommandSender sender, String target) {
        sendMessage(sender, ExApi.getNeverConnected().replace("%player%", target));
    }

    @Deprecated(since = "1.0.0")
    public void noConsole(CommandSender sender) {
        sendMessage(sender, ExApi.getConsoleMessage());
    }

    @Deprecated(since = "1.0.0")
    public void invalidityAmount(CommandSender sender) {
        sendMessage(sender, ExApi.getInvalidityAmountMessage());
    }

    @Deprecated(since = "1.0.0")
    public void noOnline(CommandSender sender, String target) {
        sendMessage(sender, ExApi.getNoOnlineMessage().replace("%player%", target));
    }

    @Deprecated(since = "1.0.0")
    public void help(CommandSender sender, String... args) {
        help(sender, args == null ? List.of() : Arrays.asList(args));
    }

    @Deprecated(since = "1.0.0")
    public void help(CommandSender sender, List<String> args) {
        sendMessage(sender, ExApi.getUsage().replace("%command%", getName()));

        if (args != null && !args.isEmpty()) {
            sendMessage(sender, " ");
            for (String arg : args) {
                sendMessage(sender, arg);
            }
            sendMessage(sender, " ");
        }

        if (sender.isOp() || hasOp(sender)) {
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

            if (!permission.isEmpty()) {
                sendMessage(sender, ExApi.getSubsPermissions());
                List<String> list = new ArrayList<>();
                for (Map.Entry<String, String> map : permission.entrySet()) {
                    list.add("&f" + map.getKey() + " &7» " + ExApi.getPlugin().getName().toLowerCase() + ".command." + getName() + "." + map.getValue());
                }
                sendMessage(sender, list);
            }
        }
    }


    @Override @Deprecated(since = "1.0.0")
    public abstract boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args);

    @Override @Deprecated(since = "1.0.0")
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        return List.of();
    }

}