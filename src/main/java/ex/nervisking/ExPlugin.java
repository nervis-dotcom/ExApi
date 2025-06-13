package ex.nervisking;

import ex.nervisking.ModelManager.ExPl;
import ex.nervisking.ModelManager.Plugins;
import ex.nervisking.ModelManager.Scheduler;
import ex.nervisking.ModelManager.Task;
import ex.nervisking.exceptions.MenuManagerException;
import ex.nervisking.exceptions.MenuManagerNotSetupException;
import ex.nervisking.menuManager.*;
import ex.nervisking.utils.Utils;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class ExPlugin extends JavaPlugin {

    private Task gui;
    private UtilsManagers utilsManagers;
    private Utils utils;
    public CommandManager commandManager;

    protected abstract void Load();
    protected abstract void Enabled();
    protected abstract void Disable();
    protected abstract boolean menu();

    @Override
    public void onLoad() {
        this.Load();
    }

    @Override
    public void onEnable() {
        this.utilsManagers = new UtilsManagers();
        this.utils = new Utils();

        if (menu()) {
            registerMenuListener();
            this.gui = Scheduler.runTimer(new updateMenus(), 0, 20);
        }
        this.Enabled();
    }

    @Override
    public void onDisable() {
        if (gui != null) {
            this.gui.cancel();
        }
        this.Disable();
    }

    private void registerMenuListener() {
        boolean isAlreadyRegistered = false;
        RegisteredListener[] var3 = InventoryClickEvent.getHandlerList().getRegisteredListeners();

        for (RegisteredListener rl : var3) {
            if (rl.getListener() instanceof MenuListener) {
                isAlreadyRegistered = true;
                break;
            }
        }

        if (!isAlreadyRegistered) {
            getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        }
    }

    public void removePlayerMenuUtility(Player p) {
        if (!menu()) return;
        playerMenuUtility.remove(p);
    }

    public void closeInventorys() {
        if (!menu()) return;
        for (Player player : getServer().getOnlinePlayers()) {
            Inventory topInventory = InventoryUtils.getTopInventory(player);
            if (topInventory.getHolder() instanceof Menu) {
                player.closeInventory();
                playerMenuUtility.remove(player);
            }
        }
    }

    public void openMenu(Class<? extends Menu> menuClass, Player player) throws MenuManagerException, MenuManagerNotSetupException {
        try {
            menuClass.getConstructor(PlayerMenuUtility.class).newInstance(getPlayerMenuUtility(player)).open();
        } catch (InstantiationException var3) {
            throw new MenuManagerException("No se pudo crear una instancia de la clase de menú", var3);
        } catch (IllegalAccessException var4) {
            throw new MenuManagerException("Acceso ilegal al intentar instanciar la clase de menú", var4);
        } catch (InvocationTargetException var5) {
            throw new MenuManagerException("Se produjo un error al intentar invocar el constructor de la clase de menú", var5);
        } catch (NoSuchMethodException var6) {
            throw new MenuManagerException("No se pudo encontrar el constructor de la clase de menú", var6);
        }
    }

    public PlayerMenuUtility getPlayerMenuUtility(Player player) throws MenuManagerNotSetupException {
        if (!menu()) {
            throw new MenuManagerNotSetupException();
        } else if (!playerMenuUtility.containsKey(player)) {
            PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
            this.playerMenuUtility.put(player, playerMenuUtility);
            return playerMenuUtility;
        } else {
            return playerMenuUtility.get(player);
        }
    }

    public UtilsManagers getUtilsManagers() {
        return utilsManagers;
    }

    public Utils getUtils() {
        return utils;
    }
}
