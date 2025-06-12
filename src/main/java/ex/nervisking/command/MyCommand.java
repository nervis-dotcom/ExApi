package ex.nervisking.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MyCommand extends BaseCommand {

    String getName();

    String getDescription();

    boolean getPermission();

    default List<String> getAliases() {
        return List.of();
    }

    boolean onCommand(CommandSender sender, Arguments args);

    default List<String> onTab(CommandSender sender, Arguments args) {
        return List.of();
    }

    @Override
    default boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String @NotNull [] args) {
        return onCommand(sender, new Arguments(args));
    }

    @Override
    default List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        return onTab(sender, new Arguments(args));
    }

}
