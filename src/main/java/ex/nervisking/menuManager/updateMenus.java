package ex.nervisking.menuManager;

import ex.nervisking.ExApi;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class updateMenus extends BukkitRunnable {

    private static final int LEVEL_OPEN = 0;
    private static final int LEVEL_RELOAD = 1;
    private static final int LEVEL_REFRESH = 2;

    @Override
    public void run() {
        for (Map.Entry<Player, PlayerMenuUtility> entry : ExApi.getPlayerMenuUtilityMap().entrySet()) {
            Player player = entry.getKey();
            Inventory topInventory = InventoryUtils.getTopInventory(player);

            if (!(topInventory.getHolder() instanceof Menu menu)) continue;
            if (!menu.update()) continue;

            int level = menu.levelUpdate();

            if (level == LEVEL_OPEN) {
                menu.open();
            } else if (level == LEVEL_RELOAD) {
                menu.reloadItems();
            } else if (level == LEVEL_REFRESH && menu instanceof MenuPages menuPages) {
                menuPages.refreshData();
            }
        }
    }
}