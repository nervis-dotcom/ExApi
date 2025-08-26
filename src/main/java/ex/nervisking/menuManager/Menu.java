package ex.nervisking.menuManager;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Pattern.ToUse;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("deprecation")
public abstract class Menu<T extends JavaPlugin> extends UtilsManagers implements InventoryHolder {

    protected final T plugin;
    protected PlayerMenuUtility playerMenuUtility;
    protected Player player;
    protected Inventory inventory;
    protected ItemStack FILTER;
    protected int pages;
    protected int totalPages;

    @SuppressWarnings("unchecked")
    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.plugin = ExApi.getPluginOf((Class<T>) JavaPlugin.class);
        this.FILTER = new ItemBuilder(ItemBuilder.GRAY).setHideTooltip().build();
        this.playerMenuUtility = playerMenuUtility;
        this.player = playerMenuUtility.getOwner();
        this.pages = 1;
        this.totalPages = 1;
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

    public void back() {
        if (this.playerMenuUtility.lastMenu() != null) {
            this.playerMenuUtility.lastMenu().open();
        }
    }

    protected void reload() {
        this.open();
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

    @ToUse
    public void setFilter() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.getItem(i) == null) {
                this.inventory.setItem(i, this.FILTER);
            }
        }
    }

    @ToUse
    public void setItemFilter(ItemBuilder itemBuilder) {
        this.FILTER = itemBuilder.build();
    }

    @ToUse
    public void setItemFilter(ItemStack itemStack) {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.getItem(i) == null) {
                this.inventory.setItem(i, itemStack);
            }
        }
    }

    @ToUse
    public void setItem(ItemStack itemStack, List<Integer> list) {
        for (int i : list) {
            if (isValidSlot(i)) {
                this.inventory.setItem(i, itemStack);
            }
        }
    }

    @ToUse
    public void setItem(ItemBuilder itemBuilder, List<Integer> list) {
        for (int i : list) {
            if (isValidSlot(i)) {
                this.inventory.setItem(i, itemBuilder.build());
            }
        }
    }

    @ToUse
    public void setItem(ItemStack itemStack, Integer... list) {
        for (int i : list) {
            if (isValidSlot(i)) {
                this.inventory.setItem(i, itemStack);
            }
        }
    }

    @ToUse
    public void setItem(ItemBuilder itemBuilder, Integer... list) {
        for (int i : list) {
            if (isValidSlot(i)) {
                this.inventory.setItem(i, itemBuilder.build());
            }
        }
    }

    @ToUse
    public void setItem(int slot, ItemStack itemStack) {
        if (isValidSlot(slot)) {
            this.inventory.setItem(slot, itemStack);
        }
    }

    @ToUse
    public void setItem(int slot, ItemBuilder itemBuilder) {
        if (isValidSlot(slot)) {
            this.inventory.setItem(slot, itemBuilder.build());
        }
    }

    @ToUse(value = "Rellena una fila completa con un ítem",
            params = {"row -> Fila (0 = primera fila, rows-1 = última fila).", "item -> Ítem a colocar."}
    )
    public void setRow(int row, ItemStack item) {
        if (inventory == null) return;
        int start = row * 9;
        for (int i = 0; i < 9; i++) {
            if (isValidSlot(start + i)) { // ← corregido
                inventory.setItem(start + i, item);
            }
        }

    }

    public void setRow(int row, ItemBuilder builder) {
        setRow(row, builder.build());
    }

    @ToUse(value = "Rellena una columna completa con un ítem",
            params = {"col -> Columna (0 = izquierda, 8 = derecha).", "item -> Ítem a colocar."}
    )
    public void setColumn(int col, ItemStack item) {
        if (inventory == null) return;
        int rows = inventory.getSize() / 9;
        for (int r = 0; r < rows; r++) {
            int slot = r * 9 + col;
            if (isValidSlot(slot)) { // validamos el slot real
                inventory.setItem(slot, item);
            }
        }

    }

    public void setColumn(int col, ItemBuilder builder) {
        setColumn(col, builder.build());
    }

    @ToUse(value = "Colocar ítems en el borde del inventario (marco).",
            params = {"item -> Ítem a colocar."}
    )
    public void setBorder(ItemStack item) {
        if (inventory == null) return;
        int size = inventory.getSize();
        int rows = size / 9;

        // fila superior
        setRow(0, item);
        // fila inferior
        setRow(rows - 1, item);

        // columnas izquierda y derecha
        for (int r = 1; r < rows - 1; r++) {

            if (isValidSlot(r)) {
                inventory.setItem(r * 9, item);     // izquierda
                inventory.setItem(r * 9 + 8, item); // derecha
            }
        }
    }

    @ToUse
    public void setBorder(ItemBuilder builder) {
        setBorder(builder.build());
    }

    @ToUse(value = "Rellena slots específicos a partir de una cadena con rangos.",
            description = "Ejemplo: 0-7,56-76,10,20,30",
            params = {"pattern -> Texto con los rangos/slots.", "item -> Ítem a colocar."}
    )
    public void fillSlots(String pattern, ItemStack item) {
        if (inventory == null || pattern == null || pattern.isEmpty()) return;

        String[] parts = pattern.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.contains("-")) {
                // Rango
                String[] range = part.split("-");
                if (range.length == 2) {
                    try {
                        int start = Integer.parseInt(range[0]);
                        int end = Integer.parseInt(range[1]);
                        for (int i = start; i <= end; i++) {

                                if (isValidSlot(i)) {
                                    inventory.setItem(i, item);
                                }
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            } else {
                // Slot único
                try {
                    int slot = Integer.parseInt(part);
                    if (isValidSlot(slot)) {
                        inventory.setItem(slot, item);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    @ToUse
    public void fillSlots(String pattern, ItemBuilder builder) {
        fillSlots(pattern, builder.build());
    }

    @ToUse(value = "Rellena una fila completa con un ítem")
    public void setRow(Row row, ItemStack item) {
        int rowIndex = row.getIndex(inventory.getSize());
        setRow(rowIndex, item);
    }

    @ToUse(value = "Rellena una columna completa con un ítem")
    public void setColumn(Column col, ItemStack item) {
        setColumn(col.getIndex(), item);
    }

    @ToUse(value = "Rellena una fila completa con un ítem")
    public void setRow(Row row, ItemBuilder builder) {
        setRow(row.getIndex(inventory.getSize()), builder);
    }

    @ToUse(value = "Rellena una columna completa con un ítem")
    public void setColumn(Column col, ItemBuilder builder) {
        setColumn(col.getIndex(), builder);
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