package ex.nervisking.Event;

import ex.nervisking.ExApi;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Event<T extends JavaPlugin> extends UtilsManagers implements Listener {

    protected final T plugin;

    @SuppressWarnings("unchecked")
    public Event() {
        this.plugin = ExApi.getPluginOf((Class<T>) JavaPlugin.class);
    }
}