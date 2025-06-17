package ex.nervisking;

import ex.nervisking.ModelManager.ExPl;
import ex.nervisking.ModelManager.Plugins;
import ex.nervisking.ModelManager.Scheduler;
import ex.nervisking.ModelManager.Task;
import ex.nervisking.command.CommandManager;
import ex.nervisking.menuManager.*;
import ex.nervisking.utils.PyfigletMessage;
import ex.nervisking.utils.Utils;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ExPlugin extends JavaPlugin {

    private Task gui;
    public UtilsManagers utilsManagers;
    public Utils utils;
    public CommandManager commandManager;
    public PyfigletMessage pyfigletMessage;

    protected void Load() {}
    protected abstract void Enabled();
    protected abstract void Disable();
    protected abstract boolean menu();

    @Override
    public void onLoad() {
        this.Load();
    }

    @Override
    public void onEnable() {
        new ExApi(this, menu());
        this.utilsManagers = ExApi.getUtilsManagers();
        this.utils = ExApi.getUtils();
        this.commandManager = new CommandManager(this);
        this.pyfigletMessage = new PyfigletMessage();

        if (menu()) {
            this.gui = Scheduler.runTimer(new updateMenus(), 0, 20);
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

    public boolean isPlugin(String plugin) {
        Plugin wgPlugin = getServer().getPluginManager().getPlugin(plugin);
        return wgPlugin != null && wgPlugin.isEnabled();
    }

    public boolean isPlugin(ExPl exPl) {
        Plugin wgPlugin = getServer().getPluginManager().getPlugin(exPl.getName());
        return wgPlugin != null && wgPlugin.isEnabled();
    }

    public boolean isPlugin(Plugins plugins) {
        Plugin wgPlugin = getServer().getPluginManager().getPlugin(plugins.getName());
        return wgPlugin != null && wgPlugin.isEnabled();
    }

    public UtilsManagers getUtilsManagers() {
        return utilsManagers;
    }

    public Utils getUtils() {
        return utils;
    }
}
