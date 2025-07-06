package ex.nervisking.command;

import ex.nervisking.ExApi;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class ExCommand extends UtilsManagers implements BaseCommand {

    private final String name;
    private final String description;
    private final boolean permissionRequired;
    private final List<String> aliases;
    private final Map<String, String> permission;

    public ExCommand() {
        this.permission = new HashMap<>();

        CommandInfo info = this.getClass().getAnnotation(CommandInfo.class);
        if (info != null) {
            this.name = info.name();
            this.description = info.description();
            this.permissionRequired = info.permission();
            this.aliases = List.of(info.aliases());
        } else {
            throw new IllegalStateException("Falta @CommandInfo en: " + getClass().getName());
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getPermission() {
        return permissionRequired;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public Map<String, String> getPermissions() {
        return permission;
    }

    public void addPermission(String description, String permission) {
        this.permission.put(description, permission);
    }

    public boolean hasPermission(CommandSender sender) {
        return hasPermission(sender, "command." + getName());
    }

    public boolean hasSubPermission(CommandSender sender, String subPermission) {
        return hasPermission(sender, "command." + getName() + "." + subPermission);
    }

    public void noPermission(CommandSender sender) {
        sendMessage(sender, ExApi.getPermissionMessage());
    }

    public void help(CommandSender sender, String... args) {
        help(sender, args == null ? List.of() : Arrays.asList(args));
    }

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

    protected List<String> filterSubcommands(Arguments args, String... options) {
        if (args.isEmpty()) return Arrays.asList(options);

        String input = args.getLast().toLowerCase();
        List<String> result = new ArrayList<>();

        for (String option : options) {
            if (option.toLowerCase().startsWith(input)) {
                result.add(option);
            }
        }
        return result;
    }

    protected List<String> filterSubcommands(CommandSender sender, Arguments args, Map<String, String> subPerms) {
        if (args.isEmpty()) return new ArrayList<>(subPerms.keySet());

        String input = args.getLast().toLowerCase();
        List<String> result = new ArrayList<>();

        for (Map.Entry<String, String> entry : subPerms.entrySet()) {
            String subcommand = entry.getKey();
            String perm = entry.getValue();
            if (subcommand.toLowerCase().startsWith(input) && hasSubPermission(sender, perm)) {
                result.add(subcommand);
            }
        }
        return result;
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        return this.onCommand(sender, new Arguments(args));
    }

    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command cmd, @NotNull String alias, String @NotNull [] args) {
        return this.onTab(sender, new Arguments(args), new ArrayList<>());
    }

    public List<String> onTab(CommandSender sender, Arguments args, List<String> completions) {
        return completions;
    }
    public abstract boolean onCommand(CommandSender sender, Arguments args);
}
