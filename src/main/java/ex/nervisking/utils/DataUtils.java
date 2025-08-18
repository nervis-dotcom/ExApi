package ex.nervisking.utils;

import ex.nervisking.ExApi;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class DataUtils {

    private static final Random RANDOM = new Random();

    public static String generateRandomSegment(String characters, int amount) {
        if (characters == null) characters = "lokijuhy24680";
        StringBuilder segment = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            segment.append(characters.charAt(RANDOM.nextInt(characters.length())));
        }
        return segment.toString();
    }

    public static String getDataFromString(ItemStack itemStack, NamespacedKey key) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.get(key, PersistentDataType.STRING);
    }

    public static boolean isDataFromString(ItemStack item, String NAMESPACE) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(ExApi.getNamespacedKey(NAMESPACE), PersistentDataType.STRING);
    }

    public static boolean getDataFromBoolean(ItemStack itemStack, NamespacedKey key){
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return Boolean.TRUE.equals(container.get(key, PersistentDataType.BOOLEAN));
    }

    public static int getDataFromInt(ItemStack itemStack, NamespacedKey key) {
        if (itemStack == null || itemStack.getItemMeta() == null) return 0;
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        Integer value = container.get(key, PersistentDataType.INTEGER);
        return value != null ? value : 0;
    }

    public static double getDataFromDouble(ItemStack itemStack, NamespacedKey key) {
        if (itemStack == null || itemStack.getItemMeta() == null) return 0.0;
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        Double value = container.get(key, PersistentDataType.DOUBLE);
        return value != null ? value : 0.0;
    }

    public static long getDataFromLong(ItemStack itemStack, NamespacedKey key){
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return -1L;
        }
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        Long value = container.get(key, PersistentDataType.LONG);
        return value != null ? value : -1L;
    }

    public static String getDataFromString(Block block, NamespacedKey key) {
        if (block.getState() instanceof TileState state) {
            PersistentDataContainer container = state.getPersistentDataContainer();
            return container.get(key, PersistentDataType.STRING);
        }
        return null;
    }

    public static byte getDataFromByte(Block block, NamespacedKey key) {
        if (block.getState() instanceof TileState state) {
            PersistentDataContainer container = state.getPersistentDataContainer();
            return container.get(key, PersistentDataType.BYTE);
        }
        return 0;
    }

    public static void saveDataInTileState(Block block, NamespacedKey key, String value) {
        if (block.getState() instanceof TileState state) {
            PersistentDataContainer container = state.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, value);
            state.update();
        }
    }
}