package ex.nervisking.menuManager;

import ex.nervisking.ExApi;
import ex.nervisking.exceptions.MenuManagerException;
import ex.nervisking.exceptions.MenuManagerNotSetupException;
import ex.nervisking.utils.ItemBuilder;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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
    protected ItemStack FILLER;
    protected int pages = 1;
    protected int total_pages = 1;

    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.FILLER = new ItemBuilder(ItemBuilder.GRAY).setHideTooltip().build();
        this.playerMenuUtility = playerMenuUtility;
        this.player = playerMenuUtility.getOwner();
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract boolean cancelAllClicks();

    public abstract boolean update();

    public abstract boolean getTopInventory();

    public abstract boolean cancelAllDrop();

    public abstract void setMenuItems();

    public abstract void handleMenu(InventoryClickEvent event) throws MenuManagerNotSetupException, MenuManagerException;

    public void handleMenuClose() {
    }

    public boolean isCancelClose() {
        return false;
    }

    public int levelUpdate() {
        return 0;
    }

    public void open() {
        this.inventory = Bukkit.createInventory(this, Math.max(9, Math.min(54, (this.getSlots() > 0 ? this.getSlots() : 1) * 9)), setPlaceholders(player, this.getMenuName()
                .replace("%page%", String.valueOf(pages))
                .replace("%total_page%", String.valueOf(total_pages))));
        this.setMenuItems();
        this.playerMenuUtility.getOwner().openInventory(this.inventory);
        this.playerMenuUtility.pushMenu(this);
    }

    public void back() throws MenuManagerException, MenuManagerNotSetupException {
        ExApi.openMenu(this.playerMenuUtility.lastMenu().getClass(), this.playerMenuUtility.getOwner());
    }

    public void reloadItems() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            this.inventory.setItem(i, null);
        }
        this.setMenuItems();
    }

    protected void reload() throws MenuManagerException, MenuManagerNotSetupException {
        this.player.closeInventory();
        ExApi.openMenu(this.getClass(), this.player);
    }

    public void closeInventory() {
        this.player.closeInventory();
    }

    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public void setItem() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.getItem(i) == null) {
                this.inventory.setItem(i, this.FILLER);
            }
        }
    }

    public void setItem(ItemBuilder itemBuilder) {
        this.FILLER = itemBuilder.build();
    }

    public void setItem(ItemStack itemStack) {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.getItem(i) == null) {
                this.inventory.setItem(i, itemStack);
            }
        }
    }

    public void setItem(ItemStack itemStack, List<Integer> list) {
        for (int i : list) {
            this.inventory.setItem(i, itemStack);
        }
    }

    public void setItem(ItemBuilder itemBuilder, List<Integer> list) {
        for (int i : list) {
            this.inventory.setItem(i, itemBuilder.build());
        }
    }

    public void setItem(ItemStack itemStack, Integer... list) {
        for (int i : list) {
            this.inventory.setItem(i, itemStack);
        }
    }

    public void setItem(ItemBuilder itemBuilder, Integer... list) {
        for (int i : list) {
            this.inventory.setItem(i, itemBuilder.build());
        }
    }

    public void setItem(int slot, ItemStack itemStack) {
        this.inventory.setItem(slot, itemStack);
    }

    public void setItem(int slot, ItemBuilder itemBuilder) {
        this.inventory.setItem(slot, itemBuilder.build());
    }

    public String getSoundForText() {
        return "UI_BUTTON_CLICK;1;1";
    }

    public Sound getSound() {
        return Sound.UI_BUTTON_CLICK;
    }
}