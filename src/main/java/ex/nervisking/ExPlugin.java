package ex.nervisking;

import ex.nervisking.Event.armorequipevent.ArmorListener;
import ex.nervisking.Event.itemselectevent.ItemSelectListener;
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

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

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ArmorListener(new ArrayList<>()), this);
        pm.registerEvents(new ItemSelectListener(this), this);
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