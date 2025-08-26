package ex.nervisking.menuManager;

import ex.nervisking.ExApi;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class MenuPages<T extends JavaPlugin> extends Menu<T> {

    protected final T plugin;
    protected int page = 0;
    private List<ItemStack> cachedItems;
    private List<Integer> slots;

    @SuppressWarnings("unchecked")
    public MenuPages(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        this.plugin = ExApi.getPluginOf((Class<T>) JavaPlugin.class);
    }

    public abstract List<ItemStack> addDataItems();

    public abstract List<Integer> setSlots();

    public abstract void setItems();

    protected List<ItemStack> getItems() {
        if (this.cachedItems == null) {
            this.cachedItems = this.addDataItems();
        }

        return this.cachedItems;
    }

    protected void invalidateCache() {
        this.cachedItems = null;
    }

    public void addItems() {
        this.pages = getCurrentPage();
        this.totalPages = getTotalPages();
        this.slots = setSlots();
        this.setItems();

        List<ItemStack> items = this.getItems();
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

    public boolean hasBack() {
        return this.getCurrentPage() > 1;
    }

    public boolean hasNext() {
        return this.getTotalPages() > this.getCurrentPage();
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
        int lastPageNumber = (totalItems - 1) / setSlots().size();
        if (this.page < lastPageNumber) {
            ++this.page;
            this.reloadItems();
            return true;
        } else {
            return false;
        }
    }

    public int getMaxItemsPerPage() {
        return setSlots().size();
    }

    public boolean getSlotIndex(int slot) {
        return setSlots().contains(slot);
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public int getCurrentPage() {
        return this.page + 1;
    }

    public int getTotalPages() {
        return (this.getItems().size() - 1) / setSlots().size() + 1;
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
        int lastPageNum = (this.getItems().size() - 1) / setSlots().size();
        if (this.page == lastPageNum) {
            return false;
        } else {
            this.page = lastPageNum;
            this.reloadItems();
            return true;
        }
    }
}