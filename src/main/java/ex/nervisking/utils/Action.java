package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Logger;
import ex.nervisking.menuManager.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Action {

    private final UtilsManagers utilsManagers;

    public Action(UtilsManagers utilsManagers) {
        this.utilsManagers = utilsManagers;
    }

    public void executeActions(Player player, List<String> actions) {
        if (actions == null || actions.isEmpty()) return;

        BukkitScheduler scheduler = Bukkit.getScheduler();
        long delay = 0L;

        for (String action : actions) {
            String trimmed = action.trim().toLowerCase();

            // Esperar en segundos
            if (trimmed.startsWith("[wait]")) {
                try {
                    int seconds = Integer.parseInt(action.substring("[wait]".length()).trim());
                    delay += (seconds * 20L); // segundos a ticks
                } catch (NumberFormatException ignored) {
                    // ignora si el número es inválido
                }
                continue;
            }

            // Esperar en ticks
            if (trimmed.startsWith("[wait_tick]")) {
                try {
                    int ticks = Integer.parseInt(action.substring("[wait_tick]".length()).trim());
                    delay += ticks;
                } catch (NumberFormatException ignored) {
                    // ignora si el número es inválido
                }
                continue;
            }

            // Ejecutar acción con delay acumulado
            if (delay > 0) {
                scheduler.runTaskLater(ExApi.getPlugin(), () -> executeAction(player, action), delay);
            } else {
                executeAction(player, action);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void executeAction(Player player, String action) {
        Inventory topInventory = InventoryUtils.getTopInventory(player);
        if (action.toLowerCase().startsWith("[has_item]")) {
            String[] split = action.substring("[has_item]".length()).trim().split("->");
            if (split.length == 2) {
                String[] itemData = split[0].trim().split(";");
                if (itemData.length == 2) {
                    Material material = Material.matchMaterial(itemData[0].trim().toUpperCase());
                    int amount = Integer.parseInt(itemData[1].trim());
                    if (material != null && hasItem(player, material, amount)) {
                        executeAction(player, split[1].trim());
                    }
                }
            }
        } else if (action.toLowerCase().startsWith("[has_permission]")) {
            String[] split = action.substring("[has_permission]".length()).trim().split("->");
            if (split.length == 2) {
                String permission = split[0].trim();
                if (player.hasPermission(permission)) {
                    executeAction(player, split[1].trim());
                }
            }
        } else if (action.toLowerCase().startsWith("[has_world]")) {
            String[] split = action.substring("[has_world]".length()).trim().split("->");
            if (split.length == 2) {
                String world = split[0].trim();
                if (player.getWorld().getName().equalsIgnoreCase(world)) {
                    executeAction(player, split[1].trim());
                }
            }
        } else if (action.toLowerCase().startsWith("[has_placeholder]")) {
            String[] split = action.substring("[has_placeholder]".length()).trim().split("->");
            if (split.length == 2) {
                String condition = split[0].trim();
                String resultAction = split[1].trim();

                // Separar placeholder, operador y valor esperado
                Matcher matcher = Pattern.compile("(.+?)\\s*(==|!=|>=|<=|>|<)\\s*(.+)").matcher(condition);
                if (!matcher.matches()) return;

                String placeholder = matcher.group(1).trim();
                String operator = matcher.group(2).trim();
                String expected = matcher.group(3).trim();

                // Reemplazar el placeholder
                String value = utilsManagers.setPlaceholders(player, placeholder);
                expected = utilsManagers.setPlaceholders(player, expected);

                // Intentar comparar como número
                try {
                    boolean conditionMet = isConditionMet(value, expected, operator);
                    if (conditionMet) executeAction(player, resultAction);
                } catch (NumberFormatException e) {
                    // Si no son números, comparar como texto
                    boolean conditionMet = switch (operator) {
                        case "==" -> value.equalsIgnoreCase(expected);
                        case "!=" -> !value.equalsIgnoreCase(expected);
                        default -> false; // Operadores de comparación no aplican a texto
                    };

                    if (conditionMet) executeAction(player, resultAction);
                }

            }
        } else if (action.startsWith("[to_targets]")) {
            String[] parts = action.substring("[to_targets]".length()).trim().split("->");
            if (parts.length < 2) return;

            String targetPart = parts[0];
            String actions = parts[1].trim();

            String[] usernames = targetPart.split(";");
            List<Player> targets = new ArrayList<>();

            for (String username : usernames) {
                Player target = Bukkit.getPlayerExact(username.trim());
                if (target != null && target.isOnline()) {
                    targets.add(target);
                }
            }

            for (Player target : targets) {
                executeAction(target, actions);
            }
        } else if (action.startsWith("[to_range]")) {
            String[] parts = action.substring("[to_range]".length()).trim().split("->");
            if (parts.length < 2) return;

            String targetPart = parts[0];
            String actions = parts[1].trim();

            String[] args = targetPart.split(";");
            if (args.length != 2) return;

            double radius = Double.parseDouble(args[0]);
            boolean includeSelf = Boolean.parseBoolean(args[1]);

            List<Player> targets = player.getWorld().getPlayers().stream()
                    .filter(p -> p.getLocation().distance(player.getLocation()) <= radius)
                    .filter(p -> includeSelf || !p.equals(player))
                    .toList();

            for (Player target : targets) {
                executeAction(target, actions);
            }
        } else if (action.startsWith("[to_world]")) {
            String[] parts = action.substring("[to_world]".length()).trim().split("->");
            if (parts.length < 2) return;

            String worldName = parts[0];
            String actions = parts[1].trim();

            World world = Bukkit.getWorld(worldName);
            if (world == null) return;

            for (Player target : world.getPlayers()) {
                executeAction(target, actions);
            }
        } else if (action.startsWith("[to_all]")) {
            String[] parts = action.substring("[to_all]".length()).trim().split("->");
            if (parts.length < 1) return;

            String actions = parts[0].trim();
            for (Player target : Bukkit.getOnlinePlayers()) {
                executeAction(target, actions);
            }
        } else if (action.startsWith("[teleport]")) {
            utilsManagers.teleport(player, action.substring("[teleport]".length()).trim());
        } else if (action.startsWith("[title]")) {
            utilsManagers.sendTitle(player, action.substring("[title]".length()).trim());
        } else if (action.startsWith("[message]")) {
            utilsManagers.sendMessage(player, action.substring("[message]".length()).trim());
        } else if (action.startsWith("[message_center]")) {
            utilsManagers. centeredMessage(player, action.substring("[message_center]".length()).trim());
        } else if (action.startsWith("[action_bar]")) {
            utilsManagers.actionBar(player, action.substring("[action_bar]".length()).trim());
        } else if (action.startsWith("[sound]")) {
            utilsManagers.playSound(player, action.substring("[sound]".length()).trim());
        } else if (action.startsWith("[console]")) {
            utilsManagers.sendConsoleCommand(player, action.substring("[console]".length()).trim());
        } else if (action.startsWith("[player_command]")) {
            utilsManagers.sendPerformCommand(player, action.substring("[player_command]".length()).trim());
        } else if (action.toLowerCase().startsWith("[effect_add]")) {
            utilsManagers.givePotionEffect(player, action.substring("[effect_add]".length()).trim());
        } else if (action.toLowerCase().startsWith("[effect_remove]")) {
            utilsManagers.removePotionEffect(player, action.substring("[effect_remove]".length()).trim());
        } else if (action.toLowerCase().startsWith("[particle]")) {
            utilsManagers.particle(player, action.substring("[particle]".length()).trim());
        } else if (action.toLowerCase().startsWith("[effect_clear]")) {
            for (PotionEffectType effectType : PotionEffectType.values()) {
                utilsManagers.removePotionEffect(player, effectType.getName());
            }
        } else if (action.toLowerCase().startsWith("[give_item]")) {
            String[] parts = action.substring("[give_item]".length()).trim().split(";");
            if (parts.length >= 2) {
                ItemBuilder item = getItemBuilder(parts);
                player.getInventory().addItem(item.build());
            }
        } else if (action.toLowerCase().startsWith("[damage]")) {
            String[] parts = action.substring("[damage]".length()).trim().split(";");
            if (parts.length >= 2) {
                double damage = Double.parseDouble(parts[0]);
                String message = parts[1];
                try {
                    Fake.setDamage(player, damage, message);
                } catch (NumberFormatException ignored) {
                }
            }
        } else if (action.equalsIgnoreCase("[kill]")) {
            player.setHealth(0.0);
        } else if (action.toLowerCase().startsWith("[heal]")) {
            String value = action.substring("[heal]".length()).trim();
            if (!value.isEmpty()) {
                try {
                    double amount = Double.parseDouble(value);
                    double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue();
                    player.setHealth(Math.min(amount, maxHealth)); // No exceder salud máxima
                } catch (NumberFormatException ignored) {
                    player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue());
                }
            } else {
                player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue());
            }
        } else if (action.toLowerCase().startsWith("[feed]")) {
            String value = action.substring("[feed]".length()).trim();
            int amount = 20;
            if (!value.isEmpty()) {
                try {
                    amount = Integer.parseInt(value);
                    player.setFoodLevel(Math.min(amount, 20));
                    player.setSaturation(Math.min(amount, 20));
                } catch (NumberFormatException ignored) {
                }
            } else {
                player.setFoodLevel(amount);
                player.setSaturation(amount);
            }
        } else if (action.toLowerCase().startsWith("[fire]")) {
            String value = action.substring("[fire]".length()).trim();
            if (!value.isEmpty()) {
                try {
                    int amount = Integer.parseInt(value);
                    player.setFireTicks(amount);
                } catch (NumberFormatException ignored) {
                }
            } else {
                player.setFireTicks(20);
            }
        } else if (action.toLowerCase().startsWith("[lightning]")) {
            String[] parts = action.substring("[lightning]".length()).trim().split(";");
            if (parts.length >= 2) {
                try {
                    double radius = Double.parseDouble(parts[0].trim());
                    double damage = Double.parseDouble(parts[1].trim());

                    Location center = player.getLocation();
                    World world = center.getWorld();
                    if (world == null) return;

                    for (Player target : world.getPlayers()) {
                        if (target.getLocation().distance(center) <= radius) {
                            // Efecto visual
                            world.strikeLightningEffect(target.getLocation());
                            // Daño
                            Fake.setDamage(player, damage, "rayo");
                        }
                    }

                } catch (NumberFormatException ignored) {
                    utilsManagers.sendLogger(Logger.WARNING,"Acción [lightning] con formato inválido: " + action);
                }
            }
        } else if (action.toLowerCase().startsWith("[freeze]")) {
            String value = action.substring("[freeze]".length()).trim();
            if (!value.isEmpty()) {
                try {
                    int amount = Integer.parseInt(value);
                    player.setFreezeTicks(amount);
                } catch (NumberFormatException ignored) {
                }
            } else {
                player.setFreezeTicks(20);
            }
        } else if (action.equalsIgnoreCase("[extinguish]")) {
            player.setFireTicks(0);
        } else if (action.toLowerCase().startsWith("[fly]")) {
            String value = action.substring("[fly]".length()).trim();
            if (!value.isEmpty()) {
                boolean is = Boolean.parseBoolean(value);
                player.setAllowFlight(is);
                player.setFlying(is);
            }
        } else if (action.toLowerCase().startsWith("[gamemode]")) {
            try {
                String mode = action.substring("[gamemode]".length()).trim().toUpperCase();
                player.setGameMode(GameMode.valueOf(mode));
            } catch (IllegalArgumentException ignored) {}
        } else if (action.equalsIgnoreCase("[clear_armor]")) {
            player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR)});
        } else if (action.toLowerCase().startsWith("[remove_item]")) {
            String[] parts = action.substring("[remove_item]".length()).trim().split(":");
            if (parts.length >= 2) {
                Material material = Material.matchMaterial(parts[0].toUpperCase());
                int amount = Integer.parseInt(parts[1]);
                if (material != null) {
                    ItemStack toRemove = new ItemStack(material, amount);
                    player.getInventory().removeItem(toRemove);
                }
            }
        } else if (action.toLowerCase().startsWith("[launch]")) {
            try {
                double power = Double.parseDouble(action.substring("[launch]".length()).trim());
                player.setVelocity(new Vector(0, power, 0));
            } catch (NumberFormatException ignored) {}
        } else if (action.toLowerCase().startsWith("[velocity]")) {
            String[] parts = action.substring("[velocity]".length()).trim().split(";");
            if (parts.length >= 3) {
                try {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double z = Double.parseDouble(parts[2]);
                    player.setVelocity(new Vector(x, y, z));
                } catch (NumberFormatException ignored) {}
            }
        } else if (action.toLowerCase().startsWith("[broadcast]")) {
            utilsManagers.broadcastMessage(action.substring("[broadcast]".length()).trim());
        } else if (action.equalsIgnoreCase("[clear_inventory]")) {
            player.getInventory().clear();
        } else if (action.equalsIgnoreCase("[close_inventory]")) {
            player.closeInventory();
        } else if (action.equalsIgnoreCase("[refresh_inventory]")) {
            if (topInventory.getHolder() instanceof Menu menu) {
                switch (menu) {
                    case CustomizablePaginatedMenu customizablePaginatedMenu -> customizablePaginatedMenu.refreshData();
                    case PaginatedMenu paginatedMenu -> paginatedMenu.refreshData();
                    case MenuPages menuPages -> menuPages.refreshData();
                    default -> menu.reloadItems();
                }
            }
        } else if (action.equalsIgnoreCase("[inventory_first-page]")) {
            if (topInventory.getHolder() instanceof Menu menu) {
                switch (menu) {
                    case CustomizablePaginatedMenu customizablePaginatedMenu -> customizablePaginatedMenu.firstPage();
                    case PaginatedMenu paginatedMenu -> paginatedMenu.firstPage();
                    case MenuPages menuPages -> menuPages.firstPage();
                    default -> {}
                }
            }
        } else if (action.equalsIgnoreCase("[inventory_last-page]")) {
            if (topInventory.getHolder() instanceof Menu menu) {
                switch (menu) {
                    case CustomizablePaginatedMenu customizablePaginatedMenu -> customizablePaginatedMenu.lastPage();
                    case PaginatedMenu paginatedMenu -> paginatedMenu.lastPage();
                    case MenuPages menuPages -> menuPages.lastPage();
                    default -> {}
                }
            }
        } else if (action.equalsIgnoreCase("[inventory_prev-page]")) {
            if (topInventory.getHolder() instanceof Menu menu) {
                switch (menu) {
                    case CustomizablePaginatedMenu customizablePaginatedMenu -> customizablePaginatedMenu.prevPage();
                    case PaginatedMenu paginatedMenu -> paginatedMenu.prevPage();
                    case MenuPages menuPages -> menuPages.prevPage();
                    default -> {}
                }
            }
        } else if (action.equalsIgnoreCase("[inventory_next-page]")) {
            if (topInventory.getHolder() instanceof Menu menu) {
                switch (menu) {
                    case CustomizablePaginatedMenu customizablePaginatedMenu -> customizablePaginatedMenu.nextPage();
                    case PaginatedMenu paginatedMenu -> paginatedMenu.nextPage();
                    case MenuPages menuPages -> menuPages.nextPage();
                    default -> {}
                }
            }
        }
    }

    private ItemBuilder getItemBuilder(String[] parts) {
        int amount = Integer.parseInt(parts[1]);
        ItemBuilder item = new ItemBuilder(parts[0], amount);
        String name = null;
        String lore = null;
        int cmd = -1;
        String flags = null;
        String enchant = null;
        for (String string : parts) {
            if (string.startsWith("name:")) {
                name = string.replace("name:", "");
            } else if (string.startsWith("lore:")) {
                lore = string.replace("lore:", "");
            } else if (string.startsWith("cmd:")) {
                cmd = Integer.parseInt(string.replace("cmd:", ""));
            } else if (string.startsWith("flags:")) {
                flags = string.replace("flags:", "");
            } else if (string.startsWith("enchant:")) {
                enchant = string.replace("enchant:", "");
            }
        }
        if (name != null) {
            item.setName(name);
        }

        if (lore != null) {
            List<String> lorelist = new ArrayList<>(Arrays.asList(lore.split(",")));
            item.setLore(lorelist);
        }

        if (cmd != -1) {
            item.setCustomModelData(cmd);
        }

        if (flags != null) {
            List<String> flagslist = new ArrayList<>(Arrays.asList(flags.split(",")));
            item.addItemFlagsByName(flagslist);
        }

        if (enchant != null) {
            for (String e : enchant.split(",")) {
                String[] strings = e.split("-");
                if (strings.length < 3) {
                    continue;
                }

                String n = strings[0];
                int l = Integer.parseInt(strings[1]);
                boolean r = Boolean.parseBoolean(strings[2]);
                item.addEnchantByName(n, l, r);
            }
        }
        return item;
    }

    private boolean isConditionMet(String value, String expected, String operator) {
        double actual = Double.parseDouble(value);
        double expectedNum = Double.parseDouble(expected);

        return switch (operator) {
            case "==" -> actual == expectedNum;
            case "!=" -> actual != expectedNum;
            case ">=" -> actual >= expectedNum;
            case "<=" -> actual <= expectedNum;
            case ">"  -> actual > expectedNum;
            case "<"  -> actual < expectedNum;
            default -> false;
        };
    }

    private boolean hasItem(Player player, Material material, int amount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
                if (count >= amount) return true;
            }
        }
        return false;
    }
}
