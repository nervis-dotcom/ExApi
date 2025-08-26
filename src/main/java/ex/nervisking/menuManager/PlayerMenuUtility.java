package ex.nervisking.menuManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.bukkit.entity.Player;

public class PlayerMenuUtility {

    private final Player owner;
    private final Stack<Menu<?>> history;
    private final Map<String, Object> dataGui;

    public PlayerMenuUtility(Player player) {
        this.owner = player;
        this.history = new Stack<>();
        this.dataGui = new HashMap<>();
    }

    public Player getOwner() {
        return this.owner;
    }

    public Menu<?> lastMenu() {
        if (!history.isEmpty()) {
            history.pop(); // El actual
            return history.isEmpty() ? null : history.peek(); // El anterior
        }
        return null;
    }

    public void pushMenu(Menu<?> menu) {
        this.history.push(menu);
    }

    public void setData(String key, Object value) {
        this.dataGui.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key, Class<T> clazz) throws ClassCastException {
        Object value = this.dataGui.get(key);
        if (value == null) return null;
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("Cannot cast " + value.getClass().getName() + " to " + clazz.getName());
    }
}