package ex.nervisking.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @since 1.0.0
 */
public interface BaseCommand extends CommandExecutor, TabCompleter {

    String getName();
    List<String> getAliases();
    String getDescription();
    boolean getPermission();

}