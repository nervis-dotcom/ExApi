package ex.nervisking.command;

import ex.nervisking.ModelManager.Pattern.ToUse;
import ex.nervisking.utils.ExLog;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

public record CommandManager(JavaPlugin plugin) {

    @ToUse(value = "Registrar comando")
    public void registerCommand(Command command) {
        String name = command.getName();
        PluginCommand pluginCommand = createPluginCommand(name);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
            if (!command.getAliases().isEmpty()) {
                pluginCommand.setAliases(command.getAliases());
            }
            if (!command.getDescription().isEmpty()) {
                pluginCommand.setDescription(command.getDescription());
            }
            if (command.getPermission()) {
                String permiso = ".command." + command.getName();
                if (command.per() != null && !command.per().isEmpty()) {
                    permiso = command.per();
                }
                pluginCommand.setPermission(plugin.getName().toLowerCase() + permiso);
            }

            registerBukkitCommand(pluginCommand);
        } else {
            ExLog.sendWarning("No se pudo registrar el comando: " + name);
        }
    }

    @ToUse(value = "Registrar comando")
    public void registerCommand(CommandExecutor command) {
        String name = command.getName();
        PluginCommand pluginCommand = createPluginCommand(name);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
            if (!command.getAliases().isEmpty()) {
                pluginCommand.setAliases(command.getAliases());
            }
            if (!command.getDescription().isEmpty()) {
                pluginCommand.setDescription(command.getDescription());
            }
            if (command.getPermission()) {
                String permiso = ".command." + command.getName();
                if (command.per() != null && !command.per().isEmpty()) {
                    permiso = command.per();
                }
                pluginCommand.setPermission(plugin.getName().toLowerCase() + permiso);
            }

            registerBukkitCommand(pluginCommand);
        } else {
            ExLog.sendWarning("No se pudo registrar el comando: " + name);
        }
    }

    @ToUse(value = "Registrar comando")
    public void registerCommand(CustomCommand command) {
        String name = command.getName();
        PluginCommand pluginCommand = createPluginCommand(name);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
            if (!command.getAliases().isEmpty()) {
                pluginCommand.setAliases(command.getAliases());
            }
            if (!command.getDescription().isEmpty()) {
                pluginCommand.setDescription(command.getDescription());
            }
            if (command.getPermission()) {
                String permiso = ".command." + command.getName();
                if (command.per() != null && !command.per().isEmpty()) {
                    permiso = command.per();
                }
                pluginCommand.setPermission(plugin.getName().toLowerCase() + permiso);
            }

            registerBukkitCommand(pluginCommand);
        } else {
            ExLog.sendWarning("No se pudo registrar el comando: " + name);
        }
    }

    @ToUse(value = "Eliminar comando registrado")
    public void unregisterCommand(String name) {
        try {
            Map<String, Command> knownCommands = getStringCommandMap();

            // Eliminar la entrada principal
            Command removed = knownCommands.remove(name.toLowerCase());

            if (removed != null) {
                // También eliminar aliases que apuntan a este comando
                knownCommands.entrySet().removeIf(entry -> entry.getValue() == removed);
                ExLog.sendInfo("Comando '" + name + "' eliminado correctamente.");
            } else {
                ExLog.sendWarning("No se encontró el comando '" + name + "' para eliminar.");
            }
        } catch (Exception e) {
            ExLog.sendException(e);
        }
    }

    private Map<String, Command> getStringCommandMap() throws NoSuchFieldException, IllegalAccessException {
        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

        // knownCommands es un mapa privado en SimpleCommandMap
        Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
        return knownCommands;
    }

    private PluginCommand createPluginCommand(String name) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, plugin);
        } catch (Exception e) {
            ExLog.sendException(e);
            return null;
        }
    }

    private void registerBukkitCommand(PluginCommand command) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(plugin.getName().toLowerCase(), command);
        } catch (Exception e) {
            ExLog.sendException(e);
        }
    }
}