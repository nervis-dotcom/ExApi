package ex.nervisking;

import ex.nervisking.ModelManager.*;
import ex.nervisking.command.CommandManager;
import ex.nervisking.menuManager.*;
import ex.nervisking.utils.PyfigletMessage;
import ex.nervisking.utils.Utils;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ExPlugin extends JavaPlugin {

    private Task gui;
    private UtilsManagers utilsManagers;
    private Utils utils;
    public CommandManager commandManager;
    public PyfigletMessage pyfigletMessage;

    protected void Load() {}
    protected boolean Menu() {
        return false;
    }
    public void Reload() {
        ExApi.closeInventorys();
    }
    protected abstract void Enabled();
    protected abstract void Disable();

    @Override
    public void onLoad() {
        this.Load();
    }

    @Override
    public void onEnable() {
        new ExApi(this, Menu());
        this.utilsManagers = ExApi.getUtilsManagers();
        this.utils = ExApi.getUtils();
        this.commandManager = new CommandManager(this);
        this.pyfigletMessage = new PyfigletMessage();

        if (Menu()) {
            this.gui = Scheduler.runTimer(new UpdateMenus(), 0, 20);
        }
        if (isPlugin("ExMagic")) {
            return;
        }

        this.Enabled();
    }

    @Override
    public void onDisable() {
        if (gui != null) {
            this.gui.cancel();
        }
        ExApi.closeInventorys();
        this.Disable();
    }


    public boolean isPlugin(String pluginName) {
        Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    public boolean isPlugin(ExPl exPl) {
        return this.isPlugin(exPl.getName());
    }

    public boolean isPlugin(Plugins plugins) {
        return this.isPlugin(plugins.getName());
    }

    public void setPutOut() {
        getServer().getPluginManager().disablePlugin(this);
    }

    public UtilsManagers getUtilsManagers() {
        return utilsManagers;
    }

    public Utils getUtils() {
        return utils;
    }
}