package ex.nervisking.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public interface BaseCommand extends CommandExecutor, TabCompleter {

    String getName();
    List<String> getAliases();
    String getDescription();
    boolean getPermission();

    boolean onCommand(CommandSender sender, Arguments args);

}
