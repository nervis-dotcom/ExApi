package ex.nervisking;

import ex.nervisking.ModelManager.ExPl;
import ex.nervisking.ModelManager.Pattern.KeyDef;
import ex.nervisking.ModelManager.Plugins;
import ex.nervisking.ModelManager.Pattern.ToUse;
import ex.nervisking.command.CommandManager;
import ex.nervisking.exceptions.MenuManagerException;
import ex.nervisking.exceptions.MenuManagerNotSetupException;
import ex.nervisking.menuManager.*;
import ex.nervisking.utils.ExLog;
import ex.nervisking.utils.ServerVersion;
import ex.nervisking.utils.Utils;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;

public class ExApi {

    private static JavaPlugin plugin;
    private static CommandManager commandManager;
    private static BungeeMessagingManager bungeeMessagingManager;
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

    private static String permissionMessage = "%prefix% &cNo tienes permisos para usar este comando.";
    private static String consoleMessage = "%prefix% &c¡Solo los jugadores pueden usar este comando!";
    private static String invalidityAmountMessage = "%prefix% &cDebes usar una cantidad válida.";
    private static String noOnlineMessage = "%prefix% &cEl jugador &7%player% &cno está conectado.";
    private static String neverConnected = "%prefix% &cEl jugador &7%player% &cNunca se a conectado.";

    @SuppressWarnings("deprecation")
    public ExApi(JavaPlugin plugin, boolean menu) {
        this.setVersion();
        ExApi.plugin = plugin;
        bungeeMessagingManager = new BungeeMessagingManager(plugin);
        commandManager = new CommandManager(plugin);
        utilsManagers = new UtilsManagers();
        utils = new Utils();
        sVar = plugin.getServer().getBukkitVersion().split("-")[0];
        descriptionFile = plugin.getDescription();

        if (menu) {
            registerMenuListener(plugin);
            isMenu = true;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends JavaPlugin> T getPlugin(Class<T> pluginClass) {
        return (T) plugin;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @Contract(pure = true)
    public static <T extends JavaPlugin> T getPluginOf(@NotNull Class<T> clazz) {
        return clazz.cast(plugin);
    }

    public static BungeeMessagingManager getBungeeMessagingManager() {
        return bungeeMessagingManager;
    }

    @Contract(pure = true)
    @ToUse
    public static @NotNull String getPluginName() {
        return descriptionFile != null ? descriptionFile.getName() : "unknown";
    }

    @Contract(pure = true)
    public static @NotNull String getPluginVersion() {
        return descriptionFile != null ? descriptionFile.getVersion() : "0.0.1-SNAPSHOT";
    }

    public static @NotNull String getPluginAuthor() {
        return descriptionFile != null ? descriptionFile.getAuthors().toString()
                .replace("[", "")
                .replace("]", "") : "unknown";
    }

    @ToUse
    public static PluginDescriptionFile getPluginDescriptionFile() {
        return descriptionFile;
    }

    @ToUse
    public static @NotNull Collection<? extends Player> getOnlinePlayers() {
        return plugin.getServer().getOnlinePlayers();
    }

    @ToUse
    public static CommandManager getCommandManager() {
        return commandManager;
    }

    @Contract("_ -> new")
    @ToUse
    public static @NotNull NamespacedKey getNamespacedKey(@KeyDef String name) {
        return new NamespacedKey(plugin, name);
    }

// menu ------------------------------------------------------------------------------------------------------

    private void registerMenuListener(JavaPlugin plugin) {
        boolean isAlreadyRegistered = false;
        RegisteredListener[] listeners = InventoryClickEvent.getHandlerList().getRegisteredListeners();

        for (RegisteredListener listener : listeners) {
            if (listener.getListener() instanceof MenuListener) {
                isAlreadyRegistered = true;
                break;
            }
        }

        if (!isAlreadyRegistered) {
            plugin.getServer().getPluginManager().registerEvents(new MenuListener(playerMenuUtilityMap), plugin);
        }
    }

    @ToUse(value = "Método para abir menu")
    public static void openMenuOf(@NotNull Class<? extends Menu> menuClass, Player player) throws MenuManagerException, MenuManagerNotSetupException {
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

    @ToUse(value = "Método para abir menu")
    public static void openMenu(Class<? extends Menu> menuClass, Player player) {
        try {
            ExApi.openMenuOf(menuClass, player);
        } catch (MenuManagerNotSetupException | MenuManagerException e) {
            ExLog.sendException(e);
        }
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

    // -------------------------------------------------------------------------------------------------------------

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
            case "1.21.6":
            case "1.21.7":
            case "1.21.8":
                serverVersion = ServerVersion.v1_21_R5;
                break;
            default:
                serverVersion = ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", ""));
        }
    }

    @ToUse(value = "verificar si el plugin esta activo")
    public static boolean isPlugin(String pluginName) {
        Plugin plugin = ExApi.plugin.getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    @ToUse(value = "verificar si el plugin esta activo")
    public static boolean isPlugin(@NotNull ExPl exPl) {
        return ExApi.isPlugin(exPl.getName());
    }

    @ToUse(value = "verificar si el plugin esta activo")
    public static boolean isPlugin(@NotNull Plugins plugins) {
        return ExApi.isPlugin(plugins.getName());
    }

    public static boolean isIsMenu() {
        return isMenu;
    }

    public static UtilsManagers getUtilsManagers() {
        return utilsManagers;
    }

    public static Utils getUtils() {
        return utils;
    }

    @ToUse()
    public static void setPrefix(String prefix) {
        ExApi.prefix = prefix;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static String getUsage() {
        return usage;
    }

    @ToUse()
    public static void setUsage(String usage) {
        ExApi.usage = usage;
    }

    public static String getDescription() {
        return description;
    }

    @ToUse()
    public static void setDescription(String description) {
        ExApi.description = description;
    }

    public static String getAliases() {
        return aliases;
    }

    @ToUse()
    public static void setAliases(String aliases) {
        ExApi.aliases = aliases;
    }

    public static String getPermissions() {
        return permissions;
    }

    @ToUse()
    public static void setPermissions(String permissions) {
        ExApi.permissions = permissions;
    }

    public static String getPermissionMessage() {
        return permissionMessage;
    }

    @ToUse()
    public static void setPermissionMessage(String permissionMessage) {
        ExApi.permissionMessage = permissionMessage;
    }

    public static String getConsoleMessage() {
        return consoleMessage;
    }

    @ToUse()
    public static void setConsoleMessage(String consoleMessage) {
        ExApi.consoleMessage = consoleMessage;
    }

    public static String getInvalidityAmountMessage() {
        return invalidityAmountMessage;
    }

    @ToUse()
    public static void setInvalidityAmountMessage(String invalidityAmountMessage) {
        ExApi.invalidityAmountMessage = invalidityAmountMessage;
    }

    public static String getNoOnlineMessage() {
        return noOnlineMessage;
    }

    @ToUse()
    public static void setNoOnlineMessage(String noOnlineMessage) {
        ExApi.noOnlineMessage = noOnlineMessage;
    }

    public static String getNeverConnected() {
        return neverConnected;
    }

    @ToUse()
    public static void setNeverConnected(String neverConnected) {
        ExApi.neverConnected = neverConnected;
    }

    public static String getsVar() {
        return sVar;
    }
}