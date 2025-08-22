package ex.nervisking;

import ex.nervisking.Event.EventsManager;
import ex.nervisking.ModelManager.*;
import ex.nervisking.ModelManager.Pattern.ToUse;
import ex.nervisking.command.CommandManager;
import ex.nervisking.menuManager.*;
import ex.nervisking.utils.PyfigletMessage;
import ex.nervisking.utils.Utils;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ExPlugin extends JavaPlugin {

    private Task gui;
    private Task cache;

    private UtilsManagers utilsManagers;
    private Utils utils;

    public CommandManager commandManager;
    public EventsManager eventsManager;
    public PyfigletMessage pyfigletMessage;
    public PermissionCache permissionCache;

    protected void Load() {}
    protected boolean Menu() {
        return false;
    }
    protected boolean CacheClear() {
        return false;
    }
    @ToUse
    public void Reload() {
        ExApi.closeInventorys();
    }
    protected abstract void Enable();
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
        this.commandManager = ExApi.getCommandManager();
        this.eventsManager = ExApi.getEventsManager();
        this.pyfigletMessage = new PyfigletMessage();
        this.permissionCache = ExApi.getPermissionCache();

        if (Menu()) {
            this.gui = Scheduler.runTimer(new UpdateMenus(), 0, 20);
        }

        if (CacheClear()) {
            this.cache = Scheduler.runTimerAsync(()-> permissionCache.cleanupOldEntries(), 20L * 60, 20L * 60);
        }

        this.Enable();
    }

    @Override
    public void onDisable() {
        if (gui != null) {
            this.gui.cancel();
        }
        if (cache != null) {
            this.cache.cancel();
        }
        ExApi.closeInventorys();
        this.Disable();
    }

    @ToUse(value = "Verificar si el plugin esta activo")
    public boolean isPlugin(String pluginName) {
        Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    @ToUse(value = "Verificar si el plugin esta activo")
    public boolean isPlugin(ExPl exPl) {
        return this.isPlugin(exPl.getName());
    }

    @ToUse(value = "Verificar si el plugin esta activo")
    public boolean isPlugin(Plugins plugins) {
        return this.isPlugin(plugins.getName());
    }

    @ToUse(value = "Desactiva el plugin")
    public void setPutOut() {
        getServer().getPluginManager().disablePlugin(this);
    }

    public UtilsManagers getUtilsManagers() {
        return utilsManagers;
    }

    public Utils getUtils() {
        return utils;
    }

    @ToUse
    public PermissionCache getPermissionCache() {
        return permissionCache;
    }
}