package ex.nervisking.Event;

import ex.nervisking.ModelManager.Pattern.ToUse;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record EventsManager(JavaPlugin plugin) {

    @ToUse(value = "Registrar evento")
    public void registerEvents(Listener event) {
        if (!isListenerAlreadyRegistered(event)) {
            plugin.getServer().getPluginManager().registerEvents(event, plugin);
        }
    }

    private boolean isListenerAlreadyRegistered(Listener listener) {
        Set<RegisteredListener> allListeners = new HashSet<>();

        // Obtener todos los HandlerList de Bukkit usando reflexión
        for (Field field : HandlerList.class.getDeclaredFields()) {
            if (HandlerList.class.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    HandlerList handlerList = (HandlerList) field.get(null);
                    if (handlerList != null) {
                        allListeners.addAll(List.of(handlerList.getRegisteredListeners()));
                    }
                } catch (Exception ignored) {}
            }
        }

        // Revisar si ya existe nuestro listener
        for (RegisteredListener registered : allListeners) {
            if (registered.getListener().getClass().equals(listener.getClass())) {
                return true;
            }
        }

        return false;
    }
}