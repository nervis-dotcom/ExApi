package ex.nervisking.menuManager;

import ex.nervisking.ExApi;
import ex.nervisking.exceptions.MenuManagerException;
import ex.nervisking.exceptions.MenuManagerNotSetupException;
import ex.nervisking.utils.ItemBuilder;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("deprecation")
public abstract class Menu extends UtilsManagers implements InventoryHolder {

    protected PlayerMenuUtility playerMenuUtility;
    protected Player player;
    protected Inventory inventory;
    protected ItemStack FILTER;
    protected int pages = 1;
    protected int totalPages = 1;

    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.FILTER = new ItemBuilder(ItemBuilder.GRAY).setHideTooltip().build();
        this.playerMenuUtility = playerMenuUtility;
        this.player = playerMenuUtility.getOwner();
    }

    public Menu(Player player) throws MenuManagerNotSetupException {
        this(ExApi.getPlayerMenuUtility(player));
    }

    public abstract String setName();

    public abstract int setRows();

    public abstract void addItems();

    public abstract void handleMenu(MenuEvent event) throws MenuManagerNotSetupException, MenuManagerException;

    public boolean setCancelClicks() {
        return true;
    }

    public boolean setUpdate() {
        return false;
    }

    public boolean setTopInventory() {
        return true;
    }

    public boolean setCancelDrop() {
        return true;
    }

    public boolean setCancelClose() {
        return false;
    }

    public LevelUpdate levelUpdate() {
        return LevelUpdate.RELOAD_ITEMS;
    }

    public void handleMenuClose() {}

    public void open() {
        this.inventory = Bukkit.createInventory(this, Math.max(9, Math.min(54, (this.setRows() > 0 ? this.setRows() : 1) * 9)), setPlaceholders(player, this.setName()
                .replace("%page%", String.valueOf(pages))
                .replace("%total_page%", String.valueOf(totalPages))));
        this.addItems();
        this.playerMenuUtility.getOwner().openInventory(this.inventory);
        this.playerMenuUtility.pushMenu(this);
    }

    public void back() throws MenuManagerException, MenuManagerNotSetupException {
        ExApi.openMenuOf(this.playerMenuUtility.lastMenu().getClass(), this.playerMenuUtility.getOwner());
    }

    protected void reload() throws MenuManagerException, MenuManagerNotSetupException {
        this.player.closeInventory();
        ExApi.openMenuOf(this.getClass(), this.player);
    }

    public void closeInventory() {
        this.player.closeInventory();
    }

    public void reloadItems() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            this.inventory.setItem(i, null);
        }
        this.addItems();
    }

    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public void setFilter() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.getItem(i) == null) {
                this.inventory.setItem(i, this.FILTER);
            }
        }
    }

    public void setItemFilter(ItemBuilder itemBuilder) {
        this.FILTER = itemBuilder.build();
    }

    public void setItemFilter(ItemStack itemStack) {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.getItem(i) == null) {
                this.inventory.setItem(i, itemStack);
            }
        }
    }

    public void setItem(ItemStack itemStack, List<Integer> list) {
        for (int i : list) {
            if (isValidSlot(i)) {
                this.inventory.setItem(i, itemStack);
            }
        }
    }

    public void setItem(ItemBuilder itemBuilder, List<Integer> list) {
        for (int i : list) {
            if (isValidSlot(i)) {
                this.inventory.setItem(i, itemBuilder.build());
            }
        }
    }

    public void setItem(ItemStack itemStack, Integer... list) {
        for (int i : list) {
            if (isValidSlot(i)) {
                this.inventory.setItem(i, itemStack);
            }
        }
    }

    public void setItem(ItemBuilder itemBuilder, Integer... list) {
        for (int i : list) {
            if (isValidSlot(i)) {
                this.inventory.setItem(i, itemBuilder.build());
            }
        }
    }

    public void setItem(int slot, ItemStack itemStack) {
        if (isValidSlot(slot)) {
            this.inventory.setItem(slot, itemStack);
        }
    }

    public void setItem(int slot, ItemBuilder itemBuilder) {
        if (isValidSlot(slot)) {
            this.inventory.setItem(slot, itemBuilder.build());
        }
    }

    private boolean isValidSlot(int slot) {
        return slot >= 0 && slot < inventory.getSize();
    }

    public String getSoundForText() {
        return "UI_BUTTON_CLICK;1;1";
    }

    public Sound getSound() {
        return Sound.UI_BUTTON_CLICK;
    }
}