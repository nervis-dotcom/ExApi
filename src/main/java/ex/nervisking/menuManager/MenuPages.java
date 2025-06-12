package ex.nervisking.menuManager;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class MenuPages extends Menu {

    protected int page = 0;
    private List<ItemStack> cachedItems;

    public MenuPages(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    public abstract List<ItemStack> dataToItems();

    public abstract List<Integer> getItemSlots();

    public abstract void addCustomMenuBorder();

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
        this.pages = getCurrentPage();
        this.total_pages = getTotalPages();
        this.addCustomMenuBorder();

        List<ItemStack> items = this.getItems();
        List<Integer> slots = getItemSlots();
        int start = page * slots.size();
        int end = Math.min(start + slots.size(), items.size());

        List<ItemStack> paginatedItems = items.subList(start, end);

        for (int i = 0; i < paginatedItems.size(); i++) {
            int slot = slots.get(i);
            if (slot < this.inventory.getSize()) {
                this.inventory.setItem(slot, paginatedItems.get(i));
            }
        }
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
        int lastPageNumber = (totalItems - 1) / getItemSlots().size();
        if (this.page < lastPageNumber) {
            ++this.page;
            this.reloadItems();
            return true;
        } else {
            return false;
        }
    }

    public int getMaxItemsPerPage() {
        return getItemSlots().size();
    }

    public boolean getSlotIndex(int slot) {
        return getItemSlots().contains(slot);
    }


    public int getCurrentPage() {
        return this.page + 1;
    }

    public int getTotalPages() {
        return (this.getItems().size() - 1) / getItemSlots().size() + 1;
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
        int lastPageNum = (this.getItems().size() - 1) / getItemSlots().size();
        if (this.page == lastPageNum) {
            return false;
        } else {
            this.page = lastPageNum;
            this.reloadItems();
            return true;
        }
    }
}
