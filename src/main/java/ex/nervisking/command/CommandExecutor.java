package ex.nervisking.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @since 1.0.1
 */
public interface CommandExecutor extends BaseCommand {

    @Override
    String getName();

    @Override
    String getDescription();

    @Override
    boolean getPermission();

    @Override
    default List<String> getAliases() {
        return List.of();
    }

    @Override
    default String per() {
        return "";
    }

    void onCommand(Sender sender, Arguments args);

    default Completions onTab(Sender sender, Arguments args, Completions completions) {
        return completions;
    }

    @Override
    default boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String @NotNull [] args) {
        this.onCommand(Sender.of(sender), Arguments.of(args));
        return true;
    }

    @Override
    default List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String alias, String @NotNull [] args) {
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
}