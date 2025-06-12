package ex.nervisking.menuManager;

import java.util.HashMap;
import java.util.List;

import ex.nervisking.ModelManager.DataItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class PaginatedMenu extends Menu {

    protected List<Object> data;
    protected int page = 0;
    protected int maxItemsPerPage = 28;
    private List<ItemStack> cachedItems;
    private DataItem FirstPage = new DataItem(Material.DARK_OAK_BUTTON, "&aPrimera página");
    private DataItem Previous = new DataItem(Material.DARK_OAK_BUTTON, "&aPrevia");
    private DataItem Close = new DataItem(Material.BARRIER, "&4Cerrar");
    private DataItem Next = new DataItem(Material.DARK_OAK_BUTTON, "&aPróxima");
    private DataItem LastPage = new DataItem(Material.DARK_OAK_BUTTON, "&aÚltima página");

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    public abstract List<ItemStack> dataToItems();

    public abstract @Nullable HashMap<Integer, ItemStack> getCustomMenuBorderItems();

    public abstract boolean border();
    public abstract boolean buttons();

    public void addMenuBorder() {
        if (buttons()) {
            this.inventory.setItem(47, FirstPage.getItemStack());
            this.inventory.setItem(48, Previous.getItemStack());
            this.inventory.setItem(49, Close.getItemStack());
            this.inventory.setItem(50, Next.getItemStack());
            this.inventory.setItem(51, LastPage.getItemStack());
        }

        if (border()) {
            int i;
            for (i = 0; i < 10; ++i) {
                if (this.inventory.getItem(i) == null) {
                    this.inventory.setItem(i, super.FILLER_GLASS);
                }
            }

            this.inventory.setItem(17, super.FILLER_GLASS);
            this.inventory.setItem(18, super.FILLER_GLASS);
            this.inventory.setItem(26, super.FILLER_GLASS);
            this.inventory.setItem(27, super.FILLER_GLASS);
            this.inventory.setItem(35, super.FILLER_GLASS);
            this.inventory.setItem(36, super.FILLER_GLASS);

            for (i = 44; i < 54; ++i) {
                if (this.inventory.getItem(i) == null) {
                    this.inventory.setItem(i, super.FILLER_GLASS);
                }
            }
        }

        if (this.getCustomMenuBorderItems() != null) {
            this.getCustomMenuBorderItems().forEach((integer, itemStack) -> {
                this.inventory.setItem(integer, itemStack);
            });
        }
    }

    private void setFirstPage(Material material, String name) {
       this.FirstPage = new DataItem(material, name);
    }

    private void setPrevious(Material material, String name) {
        this.Previous = new DataItem(material, name);
    }

    private void setClose(Material material, String name) {
        this.Close = new DataItem(material, name);
    }

    private void setNext(Material material, String name) {
        this.Next = new DataItem(material, name);
    }

    private void setLastPage(Material material, String name) {
        this.LastPage = new DataItem(material, name);
    }

    protected List<ItemStack> getItems() {
        if (this.cachedItems == null) {
            this.cachedItems = this.dataToItems();
        }

        return this.cachedItems;
    }

    protected void invalidateCache() {
        this.cachedItems = null;
    }

    public void setMenuItems() {
        this.addMenuBorder();

        List<ItemStack> items = this.getItems();
        int slot = 10;

        for (int i = 0; i < this.maxItemsPerPage; ++i) {
            int index = this.maxItemsPerPage * this.page + i;
            if (index >= items.size()) {
                break;
            }

            if (slot % 9 == 8) {
                slot += 2;
            }

            this.inventory.setItem(slot, items.get(index));
            ++slot;
        }

    }

    public boolean getSlotIndex(int slot) {
        return (slot >= 10 && slot <= 16) || (slot >= 19 && slot <= 25) || (slot >= 28 && slot <= 34) || (slot >= 37 && slot <= 43);
    }

    public boolean prevPage() {
        if (this.page == 0) {
            return false;
        } else {
            --this.page;
            this.reloadItems();
            return true;
        }
    }

    public boolean nextPage() {
        int totalItems = this.getItems().size();
        int lastPageNumber = (totalItems - 1) / this.maxItemsPerPage;
        if (this.page < lastPageNumber) {
            ++this.page;
            this.reloadItems();
            return true;
        } else {
            return false;
        }
    }

    public int getMaxItemsPerPage() {
        return this.maxItemsPerPage;
    }

    public int getCurrentPage() {
        return this.page + 1;
    }

    public int getTotalPages() {
        return (this.getItems().size() - 1) / this.maxItemsPerPage + 1;
    }

    public void open() {
        this.invalidateCache();
        super.open();
    }

    public void refreshData() {
        this.invalidateCache();
        this.reloadItems();
    }

    public boolean firstPage() {
        if (this.page == 0) {
            return false;
        } else {
            this.page = 0;
            this.reloadItems();
            return true;
        }
    }

    public boolean lastPage() {
        int lastPageNum = (this.getItems().size() - 1) / this.maxItemsPerPage;
        if (this.page == lastPageNum) {
            return false;
        } else {
            this.page = lastPageNum;
            this.reloadItems();
            return true;
        }
    }
}
