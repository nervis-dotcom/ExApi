package ex.nervisking.command;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Pattern.ToUse;

import java.util.List;
import java.util.function.BiConsumer;

public class CommandBuilder {

    private final String name;
    private boolean permission;
    private String[] aliases;
    private String description;
    private BiConsumer<Sender, Arguments> commandExecutor;
    private TriFunction<Sender, Arguments, Completions, Completions> tabCompleter;
    private boolean commandSet;
    private boolean tabSet;

    public CommandBuilder(String name, boolean permission) {
        this.name = name;
        this.permission = permission;
        this.aliases = new String[0];
        this.description = "";
        this.commandExecutor = null;
        this.tabCompleter = null;
        this.commandSet = false;
        this.tabSet = false;
    }

    @ToUse(value = "Define si el comando requiere permiso")
    public CommandBuilder permission(boolean permission) {
        this.permission = permission;
        return this;
    }

    @ToUse(value = "Define los alias del comando")
    public CommandBuilder aliases(String... aliases) {
        this.aliases = aliases;
        return this;
    }

    @ToUse(value = "Define la descripción del comando")
    public CommandBuilder description(String description) {
        this.description = description;
        return this;
    }

    @ToUse(value = "Define el executor del comando (debe ser llamado primero)")
    public CommandBuilder command(BiConsumer<Sender, Arguments> executor) {
        if (commandSet) {
            throw new IllegalStateException("El método command ya fue llamado.");
        }
        if (tabSet) {
            throw new IllegalStateException("No se puede llamar a command después de tab.");
        }
        this.commandExecutor = executor;
        commandSet = true;
        return this;
    }

    @ToUse(value = "Define el completado del comando (debe ser llamado después de command)")
    public CommandBuilder tab(TriFunction<Sender, Arguments, Completions, Completions> tabCompleter) {
        if (!commandSet) {
            throw new IllegalStateException("Debe llamar a command antes de tab.");
        }
        if (tabSet) {
            throw new IllegalStateException("El método tab ya fue llamado.");
        }
        this.tabCompleter = tabCompleter;
        tabSet = true;
        return this;
    }

    @ToUse(value = "Construye la instancia de Command")
    public Command build() {
        if (!commandSet) {
            throw new IllegalStateException("Debe llamar a command antes de build.");
        }

        // En caso no se definió tabCompleter, se deja uno por defecto
        TriFunction<Sender, Arguments, Completions, Completions> tabFunc = (tabCompleter != null) ? tabCompleter : (sender, args, completions) -> completions;

        return new Command() {
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
                return permission;
            }

            @Override
            public List<String> getAliases() {
                return List.of(aliases);
            }

            @Override
            public void onCommand(Sender sender, Arguments args) {
                commandExecutor.accept(sender, args);
            }

            @Override
            public Completions onTab(Sender sender, Arguments args, Completions completions) {
                return tabFunc.apply(sender, args, completions);
            }
        };
    }

    @ToUse(value = "Registrar el comando")
    public void register() {
        if (!commandSet) {
            throw new IllegalStateException("Debe llamar a command antes de build.");
        }
        ExApi.getCommandManager().registerCommand(build());
    }
}