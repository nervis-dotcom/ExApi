package ex.nervisking.menuManager;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Scheduler;
import ex.nervisking.exceptions.MenuManagerException;
import ex.nervisking.exceptions.MenuManagerNotSetupException;
import ex.nervisking.utils.ExLog;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Objects;

public record MenuListener(HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap) implements Listener {

//    @EventHandler
//    public void onMenuClick(InventoryClickEvent event) {
//        Player player = (Player) event.getWhoClicked();
//        InventoryHolder holder = InventoryUtils.getTopInventory(event).getHolder();
//        if (holder instanceof Menu menu) {
//            if (menu.setCancelClicks()) {
//                event.setCancelled(true);
//            }
//
//            Inventory topInventory = InventoryUtils.getTopInventory(player);
//            boolean isTop = menu.setTopInventory() && Objects.equals(event.getClickedInventory(), topInventory);
//
//            if (isTop || !menu.setTopInventory()) {
//                try {
//                    menu.handleMenu(event);
//                } catch (MenuManagerNotSetupException ex) {
//                    ExLog.sendError("EL ADMINISTRADOR NO SE HA CONFIGURADO. LLAME A 'Menu() {return true}' en ExPlugin");
//                } catch (MenuManagerException ex) {
//                    ExLog.sendException(ex);
//                }
//            }
//
//        }
//    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryHolder holder = InventoryUtils.getTopInventory(event).getHolder();

        if (holder instanceof Menu<?> menu) {
            if (menu.setCancelClicks()) {
                event.setCancelled(true);
            }

            Inventory topInventory = InventoryUtils.getTopInventory(player);
            boolean isTop = menu.setTopInventory() && Objects.equals(event.getClickedInventory(), topInventory);

            if (isTop || !menu.setTopInventory()) {
                try {
                    // 🔹 En lugar de pasar el InventoryClickEvent directamente
                    // creamos tu MenuEvent personalizado
                    MenuEvent menuEvent = new MenuEvent(
                            event,
                            event.getView(),
                            event.getSlotType(),
                            event.getRawSlot(),
                            event.getClick(),
                            event.getAction()
                    );

                    // Si el Menu usa tu sistema, se le pasa el MenuEvent
                    menu.handleMenu(menuEvent);

                    // Sincronizar cancelación
                    if (menuEvent.isCancelled()) {
                        event.setCancelled(true);
                    }

                } catch (MenuManagerNotSetupException ex) {
                    ExLog.sendError("EL ADMINISTRADOR NO SE HA CONFIGURADO. LLAME A 'Menu() {return true}' en ExPlugin");
                } catch (MenuManagerException ex) {
                    ExLog.sendException(ex);
                }
            }
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = InventoryUtils.getTopInventory(event).getHolder();
        if (holder instanceof Menu<?> menu) {
            if (menu.setCancelClose()) {
                Scheduler.runLater(() -> player.openInventory(event.getInventory()), 1L);
                return;
            }
            menu.handleMenuClose();
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Inventory topInventory = InventoryUtils.getTopInventory(player);
        if (topInventory.getHolder() instanceof Menu<?> menu) {
            if (menu.setCancelDrop()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * @since 1.0.1
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!ExApi.isMenu()) return;
        playerMenuUtilityMap.remove(event.getPlayer());

    }
}