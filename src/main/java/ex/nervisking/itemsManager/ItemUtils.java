package ex.nervisking.itemsManager;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import ex.nervisking.ExApi;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.profile.PlayerProfile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.Base64;

@SuppressWarnings({"deprecation"})
public class ItemUtils {

    public static TrimMaterial getTrimMaterial(String input) {
        if (input.equalsIgnoreCase("AMETHYST")) {
            return TrimMaterial.AMETHYST;
        } else if (input.equalsIgnoreCase("COPPER")) {
            return TrimMaterial.COPPER;
        } else if (input.equalsIgnoreCase("DIAMOND")) {
            return TrimMaterial.DIAMOND;
        } else if (input.equalsIgnoreCase("GOLD")) {
            return TrimMaterial.GOLD;
        } else if (input.equalsIgnoreCase("IRON")) {
            return TrimMaterial.IRON;
        }else if (input.equalsIgnoreCase("EMERALD")) {
            return TrimMaterial.EMERALD;
        }else if (input.equalsIgnoreCase("LAPIS")) {
            return TrimMaterial.LAPIS;
        }else if (input.equalsIgnoreCase("NETHERITE")) {
            return TrimMaterial.NETHERITE;
        }else if (input.equalsIgnoreCase("QUARTZ")) {
            return TrimMaterial.QUARTZ;
        }else if (input.equalsIgnoreCase("REDSTONE")) {
            return TrimMaterial.REDSTONE;
        }else if (input.equalsIgnoreCase("RESIN")) {
            return TrimMaterial.RESIN;
        }
        return null;
    }

    public static TrimPattern getTrimPattern(String input) {
        if (input.equalsIgnoreCase("DUNE")) {
            return TrimPattern.DUNE;
        } else if (input.equalsIgnoreCase("BOLT")) {
            return TrimPattern.BOLT;
        } else if (input.equalsIgnoreCase("EYE")) {
            return TrimPattern.EYE;
        } else if(input.equalsIgnoreCase("COAST")) {
            return TrimPattern.COAST;
        } else if(input.equalsIgnoreCase("RIB")) {
            return TrimPattern.RIB;
        } else if(input.equalsIgnoreCase("FLOW")) {
            return TrimPattern.FLOW;
        }  else if(input.equalsIgnoreCase("HOST")) {
            return TrimPattern.HOST;
        } else if(input.equalsIgnoreCase("RAISER")) {
            return TrimPattern.RAISER;
        } else if(input.equalsIgnoreCase("SHAPER")) {
            return TrimPattern.SHAPER;
        } else if(input.equalsIgnoreCase("SILENCE")) {
            return TrimPattern.SILENCE;
        } else if(input.equalsIgnoreCase("TIDE")) {
            return TrimPattern.TIDE;
        } else if(input.equalsIgnoreCase("VEX")) {
            return TrimPattern.VEX;
        } else if(input.equalsIgnoreCase("WARD")) {
            return TrimPattern.WARD;
        } else if(input.equalsIgnoreCase("WAYFINDER")) {
            return TrimPattern.WAYFINDER;
        } else if(input.equalsIgnoreCase("WILD")) {
            return TrimPattern.WILD;
        }
        return null;
    }

    public static ItemStack createCustomHeadItem(String texture, int amount){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        item.setAmount(amount);
        if(item.getItemMeta() instanceof SkullMeta skullMeta && texture != null){
            // Verificar versión del servidor
            if (ExApi.getServerVersion().startsWith("1.21")) {
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();
                URL url;
                try {
                    String decoded = new String(Base64.getDecoder().decode(texture));
                    String decodedFormatted = decoded.replaceAll("\\s", "");
                    JsonObject jsonObject = new Gson().fromJson(decodedFormatted, JsonObject.class);
                    String urlText = jsonObject.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();

                    url = new URL(urlText);
                } catch (Exception error) {
                    error.printStackTrace();
                    return item;
                }
                textures.setSkin(url);
                profile.setTextures(textures);

                skullMeta.setOwnerProfile(profile);
            } else {
                GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                profile.getProperties().put("textures", new Property("textures", texture));

                try {
                    Field profileField = skullMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(skullMeta, profile);
                } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
                    error.printStackTrace();
                }
            }
            item.setItemMeta(skullMeta);
        }
        return item;
    }


    @SuppressWarnings("deprecation")
    public static void createCustomHeadItem(ItemStack item, String texture) {
        if (item.getItemMeta() instanceof SkullMeta skullMeta && texture != null) {
            // Verificar versión del servidor
            if (ExApi.getServerVersion().startsWith("1.21")) {
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();
                URL url;
                try {
                    String decoded = new String(Base64.getDecoder().decode(texture));
                    String decodedFormatted = decoded.replaceAll("\\s", "");
                    JsonObject jsonObject = new Gson().fromJson(decodedFormatted, JsonObject.class);
                    String urlText = jsonObject.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();

                    url = new URL(urlText);
                } catch (Exception ignored) {
                    return;
                }
                textures.setSkin(url);
                profile.setTextures(textures);

                skullMeta.setOwnerProfile(profile);
            } else {
                GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                profile.getProperties().put("textures", new Property("textures", texture));

                try {
                    Field profileField = skullMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(skullMeta, profile);
                } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException ignored) {}
            }

            item.setItemMeta(skullMeta);
        }
    }
}
