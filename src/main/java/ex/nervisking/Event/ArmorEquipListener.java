package ex.nervisking.Event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ArmorEquipListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        InventoryType inventoryType = e.getInventory().getType();
        if (inventoryType == InventoryType.PLAYER || inventoryType == InventoryType.CRAFTING || inventoryType == InventoryType.CREATIVE) {

            int slot = e.getSlot();
            if (!isArmorSlot(slot)) return;

            ItemStack cursor = e.getCursor();

            if (isArmor(cursor)) {
                PlayerArmorEquipEvent.ArmorType type = getArmorType(cursor.getType());
                if (type != null) {
                    PlayerArmorEquipEvent event = new PlayerArmorEquipEvent(player, cursor, type);
                    Bukkit.getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        e.setCancelled(true); // Cancela el click que pondría la armadura
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRightClickArmor(final PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        if (isArmor(item)) {
            PlayerArmorEquipEvent.ArmorType type = getArmorType(item.getType());
            if (type != null) {
                PlayerArmorEquipEvent event = new PlayerArmorEquipEvent(player, item, type);
                Bukkit.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    e.setCancelled(true); // Cancela la acción de equipar armadura
                }
            }
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        for (int slot : e.getRawSlots()) {
            if (isArmorSlotDrag(slot)) {
                ItemStack dragged = e.getOldCursor();
                if (isArmor(dragged)) {
                    PlayerArmorEquipEvent.ArmorType type = getArmorType(dragged.getType());
                    if (type != null) {
                        PlayerArmorEquipEvent event = new PlayerArmorEquipEvent(player, dragged, type);
                        Bukkit.getPluginManager().callEvent(event);

                        if (event.isCancelled()) {
                            e.setCancelled(true); // Cancela la acción de arrastrar armadura
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean isArmor(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return switch (type) {
            case LEATHER_HELMET, IRON_HELMET, GOLDEN_HELMET, DIAMOND_HELMET, NETHERITE_HELMET, TURTLE_HELMET,
                 LEATHER_CHESTPLATE, IRON_CHESTPLATE, GOLDEN_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE,
                 LEATHER_LEGGINGS, IRON_LEGGINGS, GOLDEN_LEGGINGS, DIAMOND_LEGGINGS, NETHERITE_LEGGINGS,
                 LEATHER_BOOTS, IRON_BOOTS, GOLDEN_BOOTS, DIAMOND_BOOTS, NETHERITE_BOOTS -> true;
            default -> false;
        };
    }

    private boolean isArmorSlot(int slot) {
        return slot == 39 || slot == 38 || slot == 37 || slot == 36;
    }

    private boolean isArmorSlotDrag(int slot) {
        return slot == 0 || slot == 1 || slot == 2 || slot == 3;
    }

    private PlayerArmorEquipEvent.ArmorType getArmorType(Material mat) {
        return switch (mat) {
            case LEATHER_HELMET, IRON_HELMET, GOLDEN_HELMET, DIAMOND_HELMET, NETHERITE_HELMET, TURTLE_HELMET ->
                    PlayerArmorEquipEvent.ArmorType.HELMET;
            case LEATHER_CHESTPLATE, IRON_CHESTPLATE, GOLDEN_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE ->
                    PlayerArmorEquipEvent.ArmorType.CHESTPLATE;
            case LEATHER_LEGGINGS, IRON_LEGGINGS, GOLDEN_LEGGINGS, DIAMOND_LEGGINGS, NETHERITE_LEGGINGS ->
                    PlayerArmorEquipEvent.ArmorType.LEGGINGS;
            case LEATHER_BOOTS, IRON_BOOTS, GOLDEN_BOOTS, DIAMOND_BOOTS, NETHERITE_BOOTS ->
                    PlayerArmorEquipEvent.ArmorType.BOOTS;
            default -> null;
        };
    }
}
