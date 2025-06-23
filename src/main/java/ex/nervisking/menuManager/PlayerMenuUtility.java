package ex.nervisking.menuManager;

import java.util.Stack;
import org.bukkit.entity.Player;

public class PlayerMenuUtility {

    private final Player owner;
    private final Stack<Menu> history = new Stack<>();

    public PlayerMenuUtility(Player player) {
        this.owner = player;
    }

    public Player getOwner() {
        return this.owner;
    }

    public Menu lastMenu() {
        if (!history.isEmpty()) {
            history.pop(); // El actual
            return history.isEmpty() ? null : history.peek(); // El anterior
        }
        return null;
    }

    public void pushMenu(Menu menu) {
        this.history.push(menu);
    }
}
