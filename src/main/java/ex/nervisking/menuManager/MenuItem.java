package ex.nervisking.menuManager;

import ex.nervisking.ModelManager.Pattern.ToUse;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Clase que representa un ítem de menú con acceso a sus datos persistentes.
 */
public record MenuItem(ItemStack itemStack) {

    public MenuItem(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    @ToUse("Obtener el ItemStack original de este MenuItem")
    public @NotNull ItemStack itemStack() {
        return itemStack;
    }

    public @Nullable ItemDataMenu getData(NamespacedKey namespace) {
        if (itemStack == null) return null;
        return new ItemDataMenu(itemStack, namespace);
    }

    public @Nullable ItemDataMenu getData(String namespace) {
        if (itemStack == null) return null;
        return new ItemDataMenu(itemStack, namespace);
    }
}