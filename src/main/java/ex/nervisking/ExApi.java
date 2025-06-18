package ex.nervisking;

import ex.nervisking.ModelManager.ExPl;
import ex.nervisking.ModelManager.Plugins;
import ex.nervisking.exceptions.MenuManagerException;
import ex.nervisking.exceptions.MenuManagerNotSetupException;
import ex.nervisking.menuManager.*;
import ex.nervisking.utils.ServerVersion;
import ex.nervisking.utils.Utils;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ExApi {

    private static JavaPlugin plugin;
    private static UtilsManagers utilsManagers;
    private static Utils utils;
    public static ServerVersion serverVersion;

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private static boolean isMenu = false;

    private static String sVar = "0";
    private static PluginDescriptionFile descriptionFile;

    private static String prefix = "&#ffeb00&lᴇ&#ffe535&lx&#fedf6a&lᴀ&#fed99e&lᴘ&#fdd3d3&lɪ &8»";

    private static String usage = "%prefix% &fComando: /%command%";
    private static String description = "&eDescripción: &f%description%";
    private static String aliases = "&eAlias: &f%aliases%";
    private static String permissions = "&ePermiso: &f%permission%";
    private static String subsPermissions = "&ePermisos adicionales:";

    private static String permissionMessage = "%prefix% &cNo tienes permisos para usar este comando.";
    private static String consoleMessage = "%prefix% &c¡Solo los jugadores pueden usar este comando!";
    private static String invalidityAmountMessage = "%prefix% &cDebes usar una cantidad válida.";
    private static String noOnlineMessage = "%prefix% &cEl jugador &7%player% &cno está conectado.";
    private static String neverConnected = "%prefix% &cEl jugador &7%player% &cNunca se a conectado.";

    @SuppressWarnings("deprecation")
    public ExApi(JavaPlugin plugin, boolean menu) {
        this.setVersion();
        ExApi.plugin = plugin;
        utilsManagers = new UtilsManagers();
        utils = new Utils();
        sVar = plugin.getServer().getBukkitVersion().split("-")[0];
        descriptionFile = plugin.getDescription();

        if (menu) {
            registerMenuListener(plugin);
            isMenu = true;
        }
    }

    public static String getPluginVersion() {
        return descriptionFile != null ? descriptionFile.getVersion() : "0.0.1-SNAPSHOT";
    }

    public static String getPluginAuthor() {
        return descriptionFile != null ? descriptionFile.getAuthors().toString()
                .replace("[", "")
                .replace("]", "") : "null";
    }

    public static PluginDescriptionFile getPluginDescriptionFile() {
        return descriptionFile;
    }

    private void registerMenuListener(JavaPlugin plugin) {
        boolean isAlreadyRegistered = false;
        RegisteredListener[] var3 = InventoryClickEvent.getHandlerList().getRegisteredListeners();

        for (RegisteredListener rl : var3) {
            if (rl.getListener() instanceof MenuListener) {
                isAlreadyRegistered = true;
                break;
            }
        }

        if (!isAlreadyRegistered) {
            plugin.getServer().getPluginManager().registerEvents(new MenuListener(plugin), plugin);
        }
    }

    public static void openMenu(Class<? extends Menu> menuClass, Player player) throws MenuManagerException, MenuManagerNotSetupException {
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

    public static void removePlayerMenuUtility(Player p) {
        if (!isMenu) return;
        playerMenuUtilityMap.remove(p);
    }

    public static void closeInventorys() {
        if (!isMenu) return;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Inventory topInventory = InventoryUtils.getTopInventory(player);
            if (topInventory.getHolder() instanceof Menu) {
                player.closeInventory();
                playerMenuUtilityMap.remove(player);
            }
        }
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player player) throws MenuManagerNotSetupException {
        if (!isMenu) {
            throw new MenuManagerNotSetupException();
        } else if (!playerMenuUtilityMap.containsKey(player)) {
            PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
            playerMenuUtilityMap.put(player, playerMenuUtility);
            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(player);
        }
    }

    public static HashMap<Player, PlayerMenuUtility> getPlayerMenuUtilityMap() {
        return playerMenuUtilityMap;
    }

    private void setVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        switch(bukkitVersion){
            case "1.20.5":
            case "1.20.6":
                serverVersion = ServerVersion.v1_20_R4;
                break;
            case "1.21":
            case "1.21.1":
                serverVersion = ServerVersion.v1_21_R1;
                break;
            case "1.21.2":
            case "1.21.3":
                serverVersion = ServerVersion.v1_21_R2;
                break;
            case "1.21.4":
                serverVersion = ServerVersion.v1_21_R3;
                break;
            case "1.21.5":
                serverVersion = ServerVersion.v1_21_R4;
                break;
            default:
                serverVersion = ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", ""));
        }
    }

    public static boolean isPlugin(String plugin) {
        Plugin wgPlugin = ExApi.plugin.getServer().getPluginManager().getPlugin(plugin);
        return wgPlugin != null && wgPlugin.isEnabled();
    }

    public static boolean isPlugin(ExPl exPl) {
        Plugin wgPlugin = ExApi.plugin.getServer().getPluginManager().getPlugin(exPl.getName());
        return wgPlugin != null && wgPlugin.isEnabled();
    }

    public static boolean isPlugin(Plugins plugins) {
        Plugin wgPlugin = ExApi.plugin.getServer().getPluginManager().getPlugin(plugins.getName());
        return wgPlugin != null && wgPlugin.isEnabled();
    }

    public static void setIsMenu(boolean isMenu) {
        ExApi.isMenu = isMenu;
    }

    public static UtilsManagers getUtilsManagers() {
        return utilsManagers;
    }

    public static Utils getUtils() {
        return utils;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static void setPrefix(String prefix) {
        ExApi.prefix = prefix;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static String getUsage() {
        return usage;
    }

    public static void setUsage(String usage) {
        ExApi.usage = usage;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        ExApi.description = description;
    }

    public static String getAliases() {
        return aliases;
    }

    public static void setAliases(String aliases) {
        ExApi.aliases = aliases;
    }

    public static String getPermissions() {
        return permissions;
    }

    public static void setPermissions(String permissions) {
        ExApi.permissions = permissions;
    }

    public static String getPermissionMessage() {
        return permissionMessage;
    }

    public static void setPermissionMessage(String permissionMessage) {
        ExApi.permissionMessage = permissionMessage;
    }

    public static String getConsoleMessage() {
        return consoleMessage;
    }

    public static void setConsoleMessage(String consoleMessage) {
        ExApi.consoleMessage = consoleMessage;
    }

    public static String getInvalidityAmountMessage() {
        return invalidityAmountMessage;
    }

    public static void setInvalidityAmountMessage(String invalidityAmountMessage) {
        ExApi.invalidityAmountMessage = invalidityAmountMessage;
    }

    public static String getNoOnlineMessage() {
        return noOnlineMessage;
    }

    public static void setNoOnlineMessage(String noOnlineMessage) {
        ExApi.noOnlineMessage = noOnlineMessage;
    }

    public static String getSubsPermissions() {
        return subsPermissions;
    }

    public static void setSubsPermissions(String subsPermissions) {
        ExApi.subsPermissions = subsPermissions;
    }

    public static String getNeverConnected() {
        return neverConnected;
    }

    public static void setNeverConnected(String neverConnected) {
        ExApi.neverConnected = neverConnected;
    }

    public static String getsVar() {
        return sVar;
    }
}