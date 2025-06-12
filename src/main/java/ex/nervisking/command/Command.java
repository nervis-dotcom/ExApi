package ex.nervisking.command;

import ex.nervisking.ExApi;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Clase base para comandos personalizados.
 */
public abstract class Command extends UtilsManagers implements BaseCommand {

    private final Map<String, String> permission;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public Command() {
        this.permission = new HashMap<>();
    }

    // Nombre del comando
    public abstract String getName();

    // Descripción del comando
    public abstract String getDescription();

    // ¿Requiere permiso?
    public abstract boolean getPermission();

    // Aliases del comando
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    // Permisos adicionales (subpermisos)
    public Map<String, String> getPermissions() {
        return permission;
    }

    public void addPermission(String description, String permission) {
        this.permission.put(description, permission);
    }

    // Validación de permisos
    public boolean hasPermission(CommandSender sender) {
        return hasPermission(sender, "command." + getName());
    }

    public boolean hasSubPermission(CommandSender sender, String subPermission) {
        return hasPermission(sender, "command." + getName() + "." + subPermission);
    }

    // Accesos rápidos
    public boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    public Player asPlayer(CommandSender sender) {
        return (sender instanceof Player player) ? player : null;
    }

    // Cooldown básico
    public boolean isInCooldown(Player player, int seconds) {
        long now = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();
        long last = cooldowns.getOrDefault(uuid, 0L);
        if (now - last < seconds * 1000L) return true;
        cooldowns.put(uuid, now);
        return false;
    }

    // Auto-tab para subcomandos
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

    // Ayudas y mensajes
    public void noPermission(CommandSender sender) {
        sendMessage(sender, ExApi.getPermissionMessage());
    }

    public void neverConnected(CommandSender sender, String target) {
        sendMessage(sender, ExApi.getNeverConnected().replace("%player%", target));
    }

    public void noConsole(CommandSender sender) {
        sendMessage(sender, ExApi.getConsoleMessage());
    }

    public void invalidityAmount(CommandSender sender) {
        sendMessage(sender, ExApi.getInvalidityAmountMessage());
    }

    public void noOnline(CommandSender sender, String target) {
        sendMessage(sender, ExApi.getNoOnlineMessage().replace("%player%", target));
    }

    public void sendHelp(CommandSender sender, String... usages) {
        sendMessage(sender, "&eUso del comando:");
        for (String usage : usages) {
            sendMessage(sender, "&7 - /" + getName() + " " + usage);
        }
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

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, String @NotNull [] args) {
        return this.onCommand(sender, new Arguments(args));
    }

    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String alias, String @NotNull [] args) {
        return this.onTab(sender, new Arguments(args));
    }

    public abstract boolean onCommand(CommandSender sender, Arguments args);

    public List<String> onTab(CommandSender sender, Arguments args) {
        return List.of();
    }

}
