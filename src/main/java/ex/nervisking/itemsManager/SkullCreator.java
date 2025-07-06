package ex.nervisking.itemsManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullCreator {

    /** @deprecated */
    @Deprecated
    public static ItemStack itemFromName(String name) {
        ItemStack item = getPlayerSkullItem();
        return itemWithName(item, name);
    }

    /** @deprecated */
    @Deprecated
    public static ItemStack itemWithName(ItemStack item, String name) {
        notNull(item, "item");
        notNull(name, "name");
        return Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:\"" + name + "\"}");
    }

    public static ItemStack itemFromUuid(UUID id) {
        ItemStack item = getPlayerSkullItem();
        return itemWithUuid(item, id);
    }

    public static ItemStack itemWithUuid(ItemStack item, UUID id) {
        notNull(item, "item");
        notNull(id, "id");
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        Objects.requireNonNull(meta).setOwningPlayer(Bukkit.getOfflinePlayer(id));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack itemFromUrl(String url) {
        ItemStack item = getPlayerSkullItem();
        return itemWithUrl(item, url);
    }

    public static ItemStack itemWithUrl(ItemStack item, String url) {
        notNull(item, "item");
        notNull(url, "url");
        return itemWithBase64(item, urlToBase64(url));
    }

    public static ItemStack itemFromBase64(String base64) {
        ItemStack item = getPlayerSkullItem();
        return itemWithBase64(item, base64);
    }

    public static ItemStack itemWithBase64(ItemStack item, String base64) {
        notNull(item, "item");
        notNull(base64, "base64");
        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
    }

    /** @deprecated */
    @Deprecated
    public static void blockWithName(Block block, String name) {
        notNull(block, "block");
        notNull(name, "name");
        setBlockType(block);
        ((Skull)block.getState()).setOwningPlayer(Bukkit.getOfflinePlayer(name));
    }

    public static void blockWithUuid(Block block, UUID id) {
        notNull(block, "block");
        notNull(id, "id");
        setBlockType(block);
        ((Skull)block.getState()).setOwningPlayer(Bukkit.getOfflinePlayer(id));
    }

    public static void blockWithUrl(Block block, String url) {
        notNull(block, "block");
        notNull(url, "url");
        blockWithBase64(block, urlToBase64(url));
    }

    public static void blockWithBase64(Block block, String base64) {
        notNull(block, "block");
        notNull(base64, "base64");
        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        String args = String.format("%d %d %d %s", block.getX(), block.getY(), block.getZ(), "{Owner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
        if (newerApi()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "data merge block " + args);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "blockdata " + args);
        }

    }

    private static boolean newerApi() {
        try {
            Material.valueOf("PLAYER_HEAD");
            return true;
        } catch (IllegalArgumentException var1) {
            return false;
        }
    }

    private static ItemStack getPlayerSkullItem() {
        return newerApi() ? new ItemStack(Material.valueOf("PLAYER_HEAD")) : new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short)3);
    }

    private static void setBlockType(Block block) {
        try {
            block.setType(Material.valueOf("PLAYER_HEAD"), false);
        } catch (IllegalArgumentException var2) {
            block.setType(Material.valueOf("SKULL"), false);
        }

    }

    private static void notNull(Object o, String name) {
        if (o == null) {
            throw new NullPointerException(name + " should not be null!");
        }
    }

    private static String urlToBase64(String url) {
        URI actualUrl;
        try {
            actualUrl = new URI(url);
        } catch (URISyntaxException var3) {
            throw new RuntimeException(var3);
        }

        String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualUrl + "\"}}}";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }
}
