package ex.nervisking.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Logger;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public class JsonConfig {

    private final File file;
    private final Gson gson;
    private JsonObject root;

    public JsonConfig(String fileName, String folderName) {
        JavaPlugin plugin = ExApi.getPlugin();
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        // Directorio donde estará el archivo final (plugins/TuPlugin/data)
        File dir = (folderName != null) ? new File(plugin.getDataFolder(), folderName) : plugin.getDataFolder();
        if (!dir.exists()) dir.mkdirs();

        // Nombre del archivo con extensión
        String fullFileName = fileName.endsWith(".json") ? fileName : fileName + ".json";
        this.file = new File(dir, fullFileName);

        // Ruta dentro del JAR para copiar desde ahí
        String resourcePath = (folderName != null ? folderName + "/" : "") + fullFileName;

        // Copiar si no existe en disco
        if (!file.exists()) {
            if (plugin.getResource(resourcePath) != null) {
                plugin.saveResource(resourcePath, false);
                ExApi.getUtilsManagers().sendLogger(Logger.INFO, "Archivo por defecto '" + resourcePath + "' copiado al directorio de datos.");
            } else {
                try {
                    file.createNewFile();
                    root = new JsonObject();
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        this.load();
    }

    private void load() {
        try (Reader reader = new FileReader(file)) {
            this.root = gson.fromJson(reader, JsonObject.class);
            if (this.root == null) this.root = new JsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            this.root = new JsonObject();
        }
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        this.load();
    }


    // ─────────────────────────────────────
    // Métodos de escritura
    // ─────────────────────────────────────

    public void set(String path, Object value) {
        String[] keys = path.split("\\.");
        JsonObject current = root;

        for (int i = 0; i < keys.length - 1; i++) {
            if (!current.has(keys[i]) || !current.get(keys[i]).isJsonObject()) {
                current.add(keys[i], new JsonObject());
            }
            current = current.getAsJsonObject(keys[i]);
        }

        String key = keys[keys.length - 1];
        if (value == null) {
            current.remove(key);
        } else {
            current.add(key, gson.toJsonTree(value));
        }
    }

    public void remove(String path) {
        set(path, null);
    }

    public boolean contains(@NotNull String path) {
        return get(path).isPresent();
    }

    public Set<String> getKeys(@NotNull String path) {
        return get(path)
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .map(JsonObject::keySet)
                .orElse(Collections.emptySet());
    }

    // ─────────────────────────────────────
    // Getters simples
    // ─────────────────────────────────────

    public String getString(@NotNull String path, String def) {
        return get(path).map(JsonElement::getAsString).orElse(def);
    }

    public int getInt(@NotNull String path, int def) {
        return get(path).map(JsonElement::getAsInt).orElse(def);
    }

    public long getLong(@NotNull String path, long def) {
        return get(path).map(JsonElement::getAsLong).orElse(def);
    }

    public float getFloat(@NotNull String path, float def) {
        return get(path).map(JsonElement::getAsFloat).orElse(def);
    }

    public double getDouble(@NotNull String path, double def) {
        return get(path).map(JsonElement::getAsDouble).orElse(def);
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        return get(path).map(JsonElement::getAsBoolean).orElse(def);
    }

    public byte getByte(@NotNull String path, byte def) {
        return get(path).map(JsonElement::getAsByte).orElse(def);
    }

    public char getChar(@NotNull String path, char def) {
        String value = getString(path, "");
        return value.length() == 1 ? value.charAt(0) : def;
    }

    public short getShort(@NotNull String path, short def) {
        return get(path).map(JsonElement::getAsShort).orElse(def);
    }

    // ─────────────────────────────────────
    // Listas
    // ─────────────────────────────────────

    public List<String> getStringList(@NotNull String path) {
        return getTypedList(path, JsonElement::getAsString);
    }

    public List<Integer> getIntegerList(@NotNull String path) {
        return getTypedList(path, JsonElement::getAsInt);
    }

    public List<Long> getLongList(@NotNull String path) {
        return getTypedList(path, JsonElement::getAsLong);
    }

    public List<Float> getFloatList(@NotNull String path) {
        return getTypedList(path, JsonElement::getAsFloat);
    }

    public List<Double> getDoubleList(@NotNull String path) {
        return getTypedList(path, JsonElement::getAsDouble);
    }

    public List<Boolean> getBooleanList(@NotNull String path) {
        return getTypedList(path, JsonElement::getAsBoolean);
    }

    public List<Byte> getByteList(@NotNull String path) {
        return getTypedList(path, JsonElement::getAsByte);
    }

    public List<Character> getCharacterList(@NotNull String path) {
        return getTypedList(path, JsonElement::getAsCharacter);
    }

    public List<Short> getShortList(@NotNull String path) {
        return getTypedList(path, JsonElement::getAsShort);
    }

    public List<Map<String, Object>> getMapList(@NotNull String path) {
        return get(path)
                .filter(JsonElement::isJsonArray)
                .map(JsonElement::getAsJsonArray)
                .map(array -> {
                    List<Map<String, Object>> list = new ArrayList<>();
                    for (JsonElement el : array) {
                        if (el.isJsonObject()) {
                            Type type = new TypeToken<Map<String, Object>>() {}.getType();
                            Map<String, Object> map = gson.fromJson(el, type);
                            list.add(map);
                        }
                    }
                    return list;
                })
                .orElse(Collections.emptyList());
    }

    private <T> List<T> getTypedList(@NotNull String path, Function<JsonElement, T> mapper) {
        return get(path)
                .filter(JsonElement::isJsonArray)
                .map(JsonElement::getAsJsonArray)
                .map(array -> {
                    List<T> list = new ArrayList<>();
                    for (JsonElement el : array) {
                        try {
                            list.add(mapper.apply(el));
                        } catch (Exception ignored) {}
                    }
                    return list;
                })
                .orElse(Collections.emptyList());
    }

    // ─────────────────────────────────────
    // Checkers isX
    // ─────────────────────────────────────

    public boolean isString(@NotNull String path) {
        JsonElement el = get(path).orElse(null);
        return el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isString();
    }

    public boolean isInt(@NotNull String path) {
        JsonElement el = get(path).orElse(null);
        return el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber();
    }

    public boolean isLong(@NotNull String path) {
        return isInt(path);
    }

    public boolean isFloat(@NotNull String path) {
        return isInt(path);
    }

    public boolean isDouble(@NotNull String path) {
        return isInt(path);
    }

    public boolean isBoolean(@NotNull String path) {
        JsonElement el = get(path).orElse(null);
        return el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isBoolean();
    }

    public boolean isByte(@NotNull String path) {
        return isInt(path);
    }

    public boolean isChar(@NotNull String path) {
        return isString(path) && getString(path, "").length() == 1;
    }

    public boolean isShort(@NotNull String path) {
        return isInt(path);
    }

    public boolean isList(@NotNull String path) {
        JsonElement el = get(path).orElse(null);
        return el != null && el.isJsonArray();
    }

    public boolean isMapList(@NotNull String path) {
        return !getMapList(path).isEmpty();
    }

    // ─────────────────────────────────────
    // Interno: obtener elemento por path
    // ─────────────────────────────────────

    private Optional<JsonElement> get(@NotNull String path) {
        String[] keys = path.split("\\.");
        JsonElement element = root;

        for (String key : keys) {
            if (!element.isJsonObject()) return Optional.empty();
            JsonObject obj = element.getAsJsonObject();
            if (!obj.has(key)) return Optional.empty();
            element = obj.get(key);
        }

        return Optional.of(element);
    }

    // ------------------- ItemStack -------------------

    public void setItemStack(String path, ItemStack item) {
        if (item == null) {
            set(path, null);
            return;
        }

        JsonObject json = new JsonObject();

        // Material y cantidad
        json.addProperty("type", item.getType().toString());
        json.addProperty("amount", item.getAmount());

        // Meta: nombre y lore
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                json.addProperty("name", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                JsonArray loreArray = new JsonArray();
                for (String loreLine : Objects.requireNonNull(meta.getLore())) {
                    loreArray.add(loreLine);
                }
                json.add("lore", loreArray);
            }

            // Encantamientos
            if (!meta.getEnchants().isEmpty()) {
                JsonObject enchantsJson = new JsonObject();
                for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                    enchantsJson.addProperty(entry.getKey().getKey().toString(), entry.getValue());
                }
                json.add("enchantments", enchantsJson);
            }

            // Unbreakable
            json.addProperty("unbreakable", meta.isUnbreakable());
        }

        set(path, json);
    }

    public ItemStack getItemStack(@NotNull String path) {
        if (!contains(path)) return null;
        JsonElement el = get(path).orElse(null);
        if (el == null || !el.isJsonObject()) return null;

        JsonObject json = el.getAsJsonObject();

        try {
            // Material y cantidad
            org.bukkit.Material mat = org.bukkit.Material.valueOf(json.get("type").getAsString());
            int amount = json.has("amount") ? json.get("amount").getAsInt() : 1;
            ItemStack item = new ItemStack(mat, amount);

            ItemMeta meta = item.getItemMeta();

            // Nombre
            if (json.has("name")) {
                meta.setDisplayName(json.get("name").getAsString());
            }

            // Lore
            if (json.has("lore")) {
                JsonArray loreArray = json.getAsJsonArray("lore");
                List<String> lore = new ArrayList<>();
                for (JsonElement line : loreArray) {
                    lore.add(line.getAsString());
                }
                meta.setLore(lore);
            }

            // Encantamientos
            if (json.has("enchantments")) {
                JsonObject enchantsJson = json.getAsJsonObject("enchantments");
                for (Map.Entry<String, JsonElement> entry : enchantsJson.entrySet()) {
                    Enchantment enchant = Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(entry.getKey()));
                    if (enchant != null) {
                        meta.addEnchant(enchant, entry.getValue().getAsInt(), true);
                    }
                }
            }

            // Unbreakable
            if (json.has("unbreakable")) {
                meta.setUnbreakable(json.get("unbreakable").getAsBoolean());
            }

            item.setItemMeta(meta);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

// ------------------- Color -------------------

    public void setColor(String path, Color color) {
        if (color == null) {
            set(path, null);
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("r", color.getRed());
        json.addProperty("g", color.getGreen());
        json.addProperty("b", color.getBlue());
        set(path, json);
    }

    public Color getColor(@NotNull String path) {
        if (!contains(path)) return null;
        JsonElement el = get(path).orElse(null);
        if (el == null || !el.isJsonObject()) return null;
        JsonObject json = el.getAsJsonObject();
        try {
            int r = json.get("r").getAsInt();
            int g = json.get("g").getAsInt();
            int b = json.get("b").getAsInt();
            return Color.fromRGB(r, g, b);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

// ------------------- Location -------------------

    public void setLocation(String path, Location loc) {
        if (loc == null) {
            set(path, null);
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("world", loc.getWorld() != null ? loc.getWorld().getName() : "");
        json.addProperty("x", loc.getX());
        json.addProperty("y", loc.getY());
        json.addProperty("z", loc.getZ());
        json.addProperty("yaw", loc.getYaw());
        json.addProperty("pitch", loc.getPitch());
        set(path, json);
    }

    public Location getLocation(@NotNull String path) {
        if (!contains(path)) return null;
        JsonElement el = get(path).orElse(null);
        if (el == null || !el.isJsonObject()) return null;
        JsonObject json = el.getAsJsonObject();
        try {
            String worldName = json.get("world").getAsString();
            World world = ExApi.getPlugin().getServer().getWorld(worldName);
            double x = json.get("x").getAsDouble();
            double y = json.get("y").getAsDouble();
            double z = json.get("z").getAsDouble();
            float yaw = json.has("yaw") ? json.get("yaw").getAsFloat() : 0f;
            float pitch = json.has("pitch") ? json.get("pitch").getAsFloat() : 0f;
            if (world == null) return null;
            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}