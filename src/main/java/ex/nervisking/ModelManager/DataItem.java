package ex.nervisking.ModelManager;

import ex.nervisking.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public record DataItem(Material material, String string) {

    public ItemStack getItemStack() {
        return new ItemBuilder(material).setName(string).build();
    }
}
