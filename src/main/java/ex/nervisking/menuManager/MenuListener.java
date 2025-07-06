package ex.nervisking.menuManager;

import ex.nervisking.ExApi;
import ex.nervisking.exceptions.MenuManagerException;
import ex.nervisking.exceptions.MenuManagerNotSetupException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;

public class MenuListener implements Listener {

    private final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap;

    public MenuListener(HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap) {
        this.playerMenuUtilityMap = playerMenuUtilityMap;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        //InventoryHolder holder = e.getInventory().getHolder();
        InventoryHolder holder = InventoryUtils.getTopInventory(e).getHolder();
        if (holder instanceof Menu menu) {
            if (menu.cancelAllClicks()) {
                e.setCancelled(true);
            }
          //  boolean isTop = menu.getTopInventory() && Objects.equals(e.getClickedInventory(), player.getOpenInventory().getTopInventory());

            Inventory topInventory = InventoryUtils.getTopInventory(player);
            boolean isTop = menu.getTopInventory() && Objects.equals(e.getClickedInventory(), topInventory);


            if (isTop || !menu.getTopInventory()) {
                try {
                    menu.handleMenu(e);
                } catch (MenuManagerNotSetupException ex) {
                    System.out.println("EL ADMINISTRADOR NO SE HA CONFIGURADO. LLAME A EXAPI.SETUP()");
                } catch (MenuManagerException ex) {
                    ex.printStackTrace();
                }
            }

        }

    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        InventoryHolder holder = InventoryUtils.getTopInventory(e).getHolder();
        if (holder instanceof Menu menu) {
            if (menu.isCancelClose()) {
                Bukkit.getScheduler().runTaskLater(ExApi.getPlugin(), () -> player.openInventory(e.getInventory()), 1L);
                return;
            }
            menu.handleMenuClose();
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        // Inventory topInventory = player.getOpenInventory().getTopInventory();
        Inventory topInventory = InventoryUtils.getTopInventory(player);

        if (topInventory.getHolder() instanceof Menu menu) {
            if (menu.cancelAllDrop()) {
                e.setCancelled(true);
            }
        }
    }

    /**
     * @since 1.1.0
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (!ExApi.isIsMenu()) return;
        Player player = e.getPlayer();
        playerMenuUtilityMap.remove(player);
    }
}