package ex.nervisking.menuManager;

import ex.nervisking.ExApi;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class UpdateMenus extends BukkitRunnable {

    @Override
    public void run() {
        for (Map.Entry<Player, PlayerMenuUtility> entry : ExApi.getPlayerMenuUtility().entrySet()) {
            Player player = entry.getKey();
            Inventory topInventory = InventoryUtils.getTopInventory(player);

            if (!(topInventory.getHolder() instanceof Menu<?> menu)) continue;
            if (!menu.setUpdate()) continue;

            LevelUpdate level = menu.levelUpdate();

            if (level == LevelUpdate.OPEN) {
                menu.open();
            } else if (level == LevelUpdate.RELOAD_ITEMS) {
                menu.reloadItems();
            } else if (level == LevelUpdate.REFRESH_ITEMS && menu instanceof MenuPages menuPages) {
                menuPages.refreshData();
            }
        }
    }
}