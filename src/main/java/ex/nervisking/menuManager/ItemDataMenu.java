package ex.nervisking.menuManager;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Pattern.ToUse;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record ItemDataMenu(ItemStack itemStack, NamespacedKey namespace) {

    ItemDataMenu(@NotNull ItemStack itemStack, @NotNull String key) {
        this(itemStack, new NamespacedKey(ExApi.getPlugin(), key));
    }

    public ItemDataMenu(@NotNull ItemStack itemStack, @NotNull NamespacedKey namespace) {
        this.itemStack = itemStack;
        this.namespace = namespace;
    }

    @Override
    public @NotNull ItemStack itemStack() {
        return itemStack;
    }

    @Override
    public @NotNull NamespacedKey namespace() {
        return namespace;
    }

    @ToUse("Obtener el dato persistente asociado al namespace configurado, o null si no existe")
    public @Nullable String getData() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.get(namespace, PersistentDataType.STRING);
    }

    @ToUse("Obtener el dato persistente o un valor por defecto si no existe (namespace interno)")
    public @NotNull String getDataOrDefault(@NotNull String def) {
        String data = this.getData();
        return data != null ? data : def;
    }

    @ToUse("Obtener un dato persistente como entero, con valor por defecto si no existe o no es numérico")
    public int getIntData(int def) {
        String data = this.getData();
        try {
            return data != null ? Integer.parseInt(data) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @ToUse("Obtener un dato persistente como booleano, con valor por defecto si no existe")
    public boolean getBooleanData(boolean def) {
        String data = this.getData();
        return data != null ? Boolean.parseBoolean(data) : def;
    }

    @ToUse("Obtener un dato persistente como UUID, o null si no existe")
    public @Nullable UUID getDataFromUuid() {
        String data = this.getData();
        return data != null ? UUID.fromString(data) : null;
    }

    @ToUse("Obtener un dato persistente como OfflinePlayer, o null si no existe")
    public @Nullable OfflinePlayer getDataFromOfflinePlayer() {
        String data = this.getData();
        return data != null ? Bukkit.getOfflinePlayer(UUID.fromString(data)) : null;
    }
}
