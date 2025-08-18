package ex.nervisking.menuManager;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final InventoryInteractEvent interactEvent;
    private final ClickType click;
    private final InventoryAction action;
    private final InventoryType.SlotType slotType;
    private final int whichSlot;
    private final int rawSlot;
    private ItemStack current;
    private final int hotbarKey;
    private boolean cancelled = false;

    // Caché de User
    private final MenuUser cachedUser;

    public MenuEvent(InventoryInteractEvent interactEvent, @NotNull InventoryView view, InventoryType.@NotNull SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action) {
        this.interactEvent = interactEvent;
        this.cachedUser = new MenuUser((Player) interactEvent.getWhoClicked());
        this.current = null;
        this.slotType = type;
        this.rawSlot = slot;
        this.whichSlot = view.convertSlot(slot);
        this.click = click;
        this.action = action;
        this.hotbarKey = -1;
    }

    public MenuUser getMenuUser() {
        return cachedUser;
    }

    public InventoryType.@NotNull SlotType getSlotType() {
        return this.slotType;
    }

    public @Nullable ItemStack getCursor() {
        return this.interactEvent.getView().getCursor();
    }

    public @Nullable ItemStack getCurrentItem() {
        return this.slotType == InventoryType.SlotType.OUTSIDE
                ? this.current
                : interactEvent.getView().getItem(this.rawSlot);
    }

    public @Nullable MenuItem getCustomCursor() {
        ItemStack cursor = this.getCursor();
        return cursor != null ? new MenuItem(cursor) : null;
    }

    public @Nullable MenuItem getCustomCurrentItem() {
        ItemStack current = this.getCurrentItem();
        return current != null ? new MenuItem(current) : null;
    }

    public boolean isRightClick() {
        return this.click.isRightClick();
    }

    public boolean isLeftClick() {
        return this.click.isLeftClick();
    }

    public boolean isShiftClick() {
        return this.click.isShiftClick();
    }

    public void setCurrentItem(@Nullable ItemStack stack) {
        if (this.slotType == InventoryType.SlotType.OUTSIDE) {
            this.current = stack;
        } else {
            interactEvent.getView().setItem(this.rawSlot, stack);
        }
    }

    public @Nullable Inventory getClickedInventory() {
        return interactEvent.getView().getInventory(this.rawSlot);
    }

    public int getSlot() {
        return this.whichSlot;
    }

    public int getRawSlot() {
        return this.rawSlot;
    }

    public int getHotbarButton() {
        return this.hotbarKey;
    }

    public @NotNull InventoryAction getAction() {
        return this.action;
    }

    public @NotNull ClickType getClick() {
        return this.click;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}