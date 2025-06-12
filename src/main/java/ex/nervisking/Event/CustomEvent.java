package ex.nervisking.Event;

import ex.nervisking.ExApi;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CustomEvent extends UtilsManagers implements Listener {

    public JavaPlugin plugin;

    public CustomEvent() {
        this.plugin = ExApi.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}