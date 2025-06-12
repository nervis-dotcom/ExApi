package ex.nervisking.menuManager;

import ex.nervisking.ExApi;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class updateMenus extends BukkitRunnable {

    @Override
    public void run() {
        for (Map.Entry<Player, PlayerMenuUtility> playerMenuUtilityEntry : ExApi.getPlayerMenuUtilityMap().entrySet()) {
            Player player = playerMenuUtilityEntry.getKey();
            Inventory topInventory = InventoryUtils.getTopInventory(player);
            if (topInventory.getHolder() instanceof Menu menu) {
                if (menu.update()) {
                    menu.open();
                }
            }
        }
    }
}
