package ex.nervisking.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @since 1.0.1
 */
public interface CommandExecutor extends BaseCommand {

    String getName();

    String getDescription();

    boolean getPermission();

    default List<String> getAliases() {
        return List.of();
    }

    boolean onCommand(Sender sender, Arguments args);

    default List<String> onTab(Sender sender, Arguments args, Completions completions) {
        return completions.asList();
    }

    @Override
    default boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String @NotNull [] args) {
        return onCommand(Sender.of(sender), Arguments.of(args));
    }

    @Override
    default List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        return onTab(Sender.of(sender), Arguments.of(args), Completions.of());
    }
}