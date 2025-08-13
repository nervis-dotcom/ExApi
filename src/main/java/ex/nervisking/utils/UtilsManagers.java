package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Pattern.ToUse;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class UtilsManagers extends Utils {

    private final JavaPlugin plugin;
    private final HashMap<String, Long> cooldowns = new HashMap<>();

    public UtilsManagers() {
        this.plugin = ExApi.getPlugin();
    }

    public boolean sendCooldown(String string, int time){
        if (cooldowns.containsKey(string)) {
            long secondsLeft = (cooldowns.get(string) + time) - System.currentTimeMillis();
            if (secondsLeft > 0) {
                return true;
            }
        }

        cooldowns.put(string, System.currentTimeMillis());
        return false;
    }

    public boolean sendCooldown(String string){
        return sendCooldown(string, 500);
    }

    public boolean hasPermission(CommandSender sender, String permission){
        return sender.hasPermission(plugin.getName().toLowerCase() + "." + permission);
    }

    public boolean hasOp(CommandSender sender){
        return hasPermission(sender,"owner");
    }

    /**
     * Reproduce un sonido al jugador o en una ubicación.
     *
     * @param player Jugador
     * @param actionLine Formato: SONIDO;VOLUMEN;PITCH;[x,y,z,mundo]
     * <p>
     * Ejemplo:
     * {@code utilsManagers.playSound(player, "ENTITY_PLAYER_LEVELUP;1.0;1.0");}
     */
    public void playSound(Player player,String actionLine) {
        if (actionLine == null || actionLine.equals("none")) return;
        String[] sep = actionLine.split(";");
        if (sep.length < 3) {
            ExLog.sendWarning("se necesitan mínimo 3 partes para reproducir un sonido");
            return;
        }

        Sound sound;
        float volume;
        float pitch;
        try {
            sound = getSoundByName(sep[0]);
            volume = Float.parseFloat(sep[1]);
            pitch = Float.parseFloat(sep[2]);
        } catch (Exception e) {
            ExLog.sendWarning("Nombre del sonido: &c" + sep[0] + " no es válido.");
            return;
        }
        Location location = null;
        if (sep.length >= 4) {
            String[] locParameters = sep[3].split(",");
            location = new Location(
                    Bukkit.getWorld(locParameters[3]),
                    Double.parseDouble(locParameters[0]),
                    Double.parseDouble(locParameters[1]),
                    Double.parseDouble(locParameters[2])
            );
        }

        if (location != null) {
            Objects.requireNonNull(location.getWorld()).playSound(location, sound, volume, pitch);
        } else {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }

    }

    /**
     * Reproduce un sonido en una ubicación.
     *
     * @param location location
     * @param actionLine Formato: SONIDO;VOLUMEN;PITCH
     * <p>
     * Ejemplo:
     * {@code utilsManagers.playSound(location, "ENTITY_PLAYER_LEVELUP;1.0;1.0");}
     */
    public void playSound(Location location, String actionLine) {
        if (actionLine == null || actionLine.equals("none")) return;
        String[] sep = actionLine.split(";");
        if (sep.length < 3) {
            ExLog.sendWarning("se necesitan minimo 3 partes para reproducir un sonido");
            return;
        }

        Sound sound;
        float volume;
        float pitch;
        try {
            sound = getSoundByName(sep[0]);
            volume = Float.parseFloat(sep[1]);
            pitch = Float.parseFloat(sep[2]);
        } catch (Exception e) {
            ExLog.sendWarning( "Nombre del sonido: &c" + sep[0] + " no es válido.");
            return;
        }

        location.getWorld().playSound(location, sound, volume, pitch);
    }

    private Sound getSoundByName(String name){
        try {
            Class<?> soundTypeClass = Class.forName("org.bukkit.Sound");
            Method valueOf = soundTypeClass.getMethod("valueOf", String.class);
            return (Sound) valueOf.invoke(null,name);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Muestra un título y subtítulo al jugador.
     *
     * @param player Jugador
     * @param actionLine Formato: titulo;subtitulo;fadeIn;stay;fadeOut
     * <p>
     * Ejemplo:
     * {@code utilsManagers.sendTitle(player, "&aBienvenido;&eDisfruta el servidor;10;60;10");}
     */
    public void sendTitle(Player player, String actionLine) {
        if (actionLine == null || actionLine.equals("none")) return;

        String[] sep = actionLine.split(";");
        if (sep.length >= 2) {

            String title = sep[0];
            String subtitle = sep[1];
            if (title.equals("none")) {
                title = "";
            }
            if (subtitle.equals("none")) {
                subtitle = "";
            }

            int fadeIn = 10;
            int stay = 10;
            int fadeOut = 10;
            if (sep.length >= 5) {
                try {
                    fadeIn = Integer.parseInt(sep[2]);
                    stay = Integer.parseInt(sep[3]);
                    fadeOut = Integer.parseInt(sep[4]);
                } catch (NumberFormatException e) {
                    fadeIn = 10;
                    stay = 10;
                }
            }

            title = setPlaceholders(player, title);
            subtitle = setPlaceholders(player, subtitle);
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    /**
     * Envía un mensaje en la barra de acción (action bar).
     *
     * @param player Jugador
     * @param message Mensaje
     * <p>
     * Ejemplo:
     * {@code utilsManagers.actionBar(player, "&eModo Combate Activado");}
     */
    public void sendActionBar(Player player, String message) {
        if (message == null || message.isEmpty() || message.equals("none")) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(setPlaceholders(player, message)));
    }

    /**
     * Muestra un mensaje en la action bar durante un tiempo específico.
     *
     * @param player Jugador
     * @param message Mensaje
     * @param duration Duración en ticks
     * <p>
     * Ejemplo:
     * {@code utilsManagers.sendActionBar(player, "&c¡Cuidado!", 100);}
     */
    public void sendActionBar(final Player player, final String message, int duration) {
        if (message == null || message.isEmpty()) return;
        sendActionBar(player, message);

        if (duration > 0) {
            // Envía un mensaje vacío al final del tiempo límite. Permite mensajes de menos de 3 segundos para garantizar la precisión.
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, "");
                }
            }.runTaskLater(plugin, duration + 1);
        }

        // Reenvía los mensajes cada 3 segundos, para que no desaparezcan de la pantalla del reproductor.
        while (duration > 40) {
            duration -= 40;
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, message);
                }
            }.runTaskLater(plugin, duration);
        }
    }

    public void sendConsoleCommand(Player player, String... commands) {
        if (commands == null) return;
        for (String cmd : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), setPlaceholders(player, cmd));
        }
    }

    public void sendConsoleCommand(Player player, List<String> commands) {
      this.sendConsoleCommand(player, commands.toArray(new String[0]));
    }

    public void sendPerformCommand(Player player, String... command) {
        this.sendPerformCommand(player, List.of(command));
    }

    public void sendPerformCommand(Player player, List<String> command) {
        if (command == null) return;
        for (var msg : command) {
            player.performCommand(setPlaceholders(player, msg));
        }
    }

    /**
     * Invoca una o más entidades en una ubicación específica con atributos personalizados.
     * <p>
     * El string {@code actionLine} debe contener múltiples propiedades separadas por punto y coma (`;`)
     * con el siguiente formato:
     * <ul>
     *   <li>{@code location:<x>,<y>,<z>,<world>} - Ubicación donde se invoca la entidad.</li>
     *   <li>{@code entity:type} - Tipo de entidad (por ejemplo, {@code ZOMBIE}, {@code SKELETON}).</li>
     *   <li>{@code custom_name:nombre} - Nombre personalizado visible de la entidad.</li>
     *   <li>{@code health:vida} - Vida máxima e inicial de la entidad.</li>
     *   <li>{@code equipment:helmet,chestplate,leggings,boots} - Equipamiento de armadura (usar {@code none} para dejar vacío).</li>
     *   <li>{@code hand_equipment:mainhand,offhand} - Ítems en la mano principal y secundaria.</li>
     *   <li>{@code amount:cantidad} - Cantidad de entidades a invocar.</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>{@code
     * summon("location:100,64,100,world;entity:ZOMBIE;custom_name:&cZombie Boss;health:50;
     *         equipment:IRON_HELMET,none,none,none;hand_equipment:STONE_SWORD,none;amount:3");
     * }</pre>
     *
     * <p><b>Nota:</b> Usa la clase {@code ItemBuilder} personalizada para construir los ítems del equipo.</p>
     *
     * @param actionLine Línea de acción que define la entidad a invocar y sus atributos.
     */
    public void summon(String actionLine) {
        String[] sep = actionLine.replace("summon: ", "").split(";");
        Location location = null;
        EntityType type = null;

        String customName = null;
        double health = 0;
        String equipmentString = null;
        String handEquipmentString = null;
        int amount = 1;

        for (String property : sep) {
            if (property.startsWith("location:")) {
                String[] locationSplit = property.replace("location:", "").split(",");
                location = new Location(
                        Bukkit.getWorld(locationSplit[3]),
                        Double.parseDouble(locationSplit[0]),
                        Double.parseDouble(locationSplit[1]),
                        Double.parseDouble(locationSplit[2])
                );
            } else if (property.startsWith("entity:")) {
                type = EntityType.valueOf(property.replace("entity:", ""));
            } else if (property.startsWith("custom_name:")) {
                customName = property.replace("custom_name:", "");
            } else if (property.startsWith("health:")) {
                health = Double.parseDouble(property.replace("health:", ""));
            } else if (property.startsWith("equipment:")) {
                equipmentString = property.replace("equipment:", "");
            } else if (property.startsWith("hand_equipment:")) {
                handEquipmentString = property.replace("hand_equipment:", "");
            } else if (property.startsWith("amount:")) {
                amount = Integer.parseInt(property.replace("amount:", ""));
            }
        }

        if (location != null) {
            for (int i = 0; i < amount; i++) {
                Entity entity;
                if (type == null) {
                    return;
                }

                entity = location.getWorld().spawnEntity(location, type);

                if (customName != null) {
                    entity.setCustomNameVisible(true);
                    entity.setCustomName(setColoredMessage(customName));
                }

                if (entity instanceof LivingEntity livingEntity) {
                    if (health != 0) {
                        livingEntity.setMaxHealth(health);
                        livingEntity.setHealth(health);
                    }

                    EntityEquipment equipment = livingEntity.getEquipment();
                    if (equipmentString != null && equipment != null) {
                        String[] equipmentSplit = equipmentString.split(",");

                        //Helmet
                        equipment.setHelmet(!equipmentSplit[0].equals("none") ? new ItemBuilder(equipmentSplit[0]).build() : null);
                        equipment.setHelmetDropChance(0);
                        //Chestplate
                        equipment.setChestplate(!equipmentSplit[1].equals("none") ? new ItemBuilder(equipmentSplit[1]).build() : null);
                        equipment.setChestplateDropChance(0);
                        //Leggings
                        equipment.setLeggings(!equipmentSplit[2].equals("none") ? new ItemBuilder(equipmentSplit[2]).build() : null);
                        equipment.setLeggingsDropChance(0);
                        //Boots
                        equipment.setBoots(!equipmentSplit[3].equals("none") ? new ItemBuilder(equipmentSplit[3]).build() : null);
                        equipment.setBootsDropChance(0);
                    }

                    if (handEquipmentString != null && equipment != null) {
                        String[] handEquipmentSplit = handEquipmentString.split(",");

                        // Hand
                        equipment.setItemInMainHand(!handEquipmentSplit[0].equals("none") ? new ItemBuilder(handEquipmentSplit[0]).build() : null);
                        equipment.setItemInMainHandDropChance(0);
                        // Offhand
                        equipment.setItemInOffHand(!handEquipmentSplit[1].equals("none") ? new ItemBuilder(handEquipmentSplit[1]).build() : null);
                        equipment.setItemInOffHandDropChance(0);
                    }
                }

            }
        }
    }

    /**
     * Muestra partículas personalizadas en una ubicación específica o en la del jugador.
     * <p>
     * La cadena {@code actionLine} debe incluir los parámetros separados por espacios con el siguiente formato:
     * <ul>
     *   <li>{@code effect:nombre} - Nombre del efecto de partícula (por ejemplo, {@code FLAME}, {@code REDSTONE}).</li>
     *   <li>{@code offset:<x>;<y>;<z>} - Desplazamiento de partículas en cada eje.</li>
     *   <li>{@code speed:valor} - Velocidad de movimiento de las partículas.</li>
     *   <li>{@code amount:cantidad} - Cantidad de partículas a generar.</li>
     *   <li>{@code location:<x>,<y>,<z>,<world>} - Ubicación exacta donde se generarán (opcional, si no se especifica se usa la del jugador).</li>
     *   <li>{@code force:true/false} - Si las partículas deben forzarse a mostrarse a todos los jugadores.</li>
     * </ul>
     *
     * <p>Para partículas de tipo {@code REDSTONE} (alias {@code DUST}), se puede usar:</p>
     * <ul>
     *   <li>{@code effect:REDSTONE;&lt;r&gt;;&lt;g&gt;;&lt;b&gt;} - Donde {@code r}, {@code g}, {@code b} son valores RGB del color deseado.</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>{@code
     * particle(player, "effect:FLAME offset:0.5;0.5;0.5 speed:0 amount:10 force:true");
     * particle(player, "effect:REDSTONE;255;0;0 offset:0;1;0 speed:0 amount:20 location:100;65;100;world");
     * }</pre>
     *
     * @param player Jugador que ejecuta la acción (usado como ubicación si no se especifica una).
     * @param actionLine Línea de acción con los parámetros de las partículas.
     */
    public void particle(Player player,String actionLine) {
        String effectName = null;
        double offsetX = 0;
        double offsetY = 0;
        double offsetZ = 0;
        double speed = 0;
        int amount = 1;
        Location location = null;
        boolean force = false;

        String[] sep = actionLine.split(" ");
        for (String s : sep) {
            if (s.startsWith("effect:")) {
                effectName = s.replace("effect:", "");
            } else if (s.startsWith("speed:")) {
                speed = Double.parseDouble(s.replace("speed:", ""));
            } else if (s.startsWith("amount:")) {
                amount = Integer.parseInt(s.replace("amount:", ""));
            } else if (s.startsWith("offset:")) {
                String[] sep2 = s.replace("offset:", "").split(";");
                offsetX = Double.parseDouble(sep2[0]);
                offsetY = Double.parseDouble(sep2[1]);
                offsetZ = Double.parseDouble(sep2[2]);
            } else if (s.startsWith("location:")) {
                String[] sep2 = s.replace("location:", "").split(";");
                location = new Location(
                        Bukkit.getWorld(sep2[3]), Double.parseDouble(sep2[0]), Double.parseDouble(sep2[1]), Double.parseDouble(sep2[2])
                );
            } else if (s.startsWith("force:")) {
                force = Boolean.parseBoolean(s.replace("force:", ""));
            }
        }

        if (location == null) {
            location = player.getLocation();
        }

        if (effectName != null) {
            try {
                if (effectName.startsWith("REDSTONE;") || effectName.startsWith("DUST;")) {
                    String[] effectSeparated = effectName.split(";");
                    int red = Integer.parseInt(effectSeparated[1]);
                    int green = Integer.parseInt(effectSeparated[2]);
                    int blue = Integer.parseInt(effectSeparated[3]);
                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);

                    location.getWorld().spawnParticle(
                            Particle.valueOf(effectSeparated[0]), location, amount, offsetX, offsetY, offsetZ, speed, dustOptions, force);
                } else {
                    location.getWorld().spawnParticle(
                            Particle.valueOf(effectName), location, amount, offsetX, offsetY, offsetZ, speed, null, force);
                }
            } catch (Exception e) {
                ExLog.sendWarning("Nombre de la partícula: " + effectName + " no es válido.");
            }
        }
    }

    /**
     * Teletransporta al jugador a una ubicación especificada.
     *
     * @param player Jugador
     * @param actionLine Formato: mundo;x;y;z;yaw;pitch
     * <p>
     * Ejemplo:
     * {@code utilsManagers.teleport(player, "world;100;65;200;0;0");}
     */
    public void teleport(Player player, String actionLine) {
        if (actionLine != null && !actionLine.isEmpty()) {
            String[] sep = actionLine.split(";");
            if (sep.length >= 4) {
                World world = Bukkit.getWorld(sep[0]);
                double x = Double.parseDouble(sep[1]);
                double y = Double.parseDouble(sep[2]);
                double z = Double.parseDouble(sep[3]);
                float yaw = 0;
                float pitch = 0;
                if (sep.length >= 6) {
                    yaw = Float.parseFloat(sep[4]);
                    pitch = Float.parseFloat(sep[5]);
                }
                Location l = new Location(world, x, y, z, yaw, pitch);
                player.teleport(l);
            } else {
                ExLog.sendWarning("Se necesitan al menos 4 partes para teletransportar al jugador.");
            }
        }
    }

    /**
     * Aplica un efecto de poción al jugador especificado.
     *
     * <p>Formato de la acción esperada: {@code <efecto>;<duración>;<nivel>;<mostrarPartículas>}
     * <ul>
     *   <li>{@code efecto}: El nombre del efecto de poción (por ejemplo, "SPEED", "INVISIBILITY").</li>
     *   <li>{@code duración}: La duración del efecto en ticks (20 ticks = 1 segundo).</li>
     *   <li>{@code nivel}: El nivel del efecto (1 para nivel I, 2 para nivel II, etc.). Internamente se reduce en 1 para ajustarse a la API de Bukkit.</li>
     *   <li>{@code mostrarPartículas} (opcional): {@code true} o {@code false}, define si se muestran partículas. Si se omite, se asume {@code true}.</li>
     * </ul>
     *
     * <p>Ejemplo de uso:
     * <pre>{@code
     * givePotionEffect(player, "SPEED;200;2;false");
     * }</pre>
     * Esto aplicará velocidad nivel II durante 10 segundos sin mostrar partículas.
     *
     * @param player El jugador al que se aplicará el efecto.
     * @param actionLine La línea de acción que contiene los parámetros del efecto separados por punto y coma.
     */
    public void givePotionEffect(Player player, String actionLine) {
        String[] sep = actionLine.split(";");
        if (sep.length < 3) {
            return;
        }
        PotionEffectType potionEffectType = PotionEffectType.getByName(sep[0]);
        int duration = Integer.parseInt(sep[1]);
        int level = Integer.parseInt(sep[2]);
        boolean showParticles = true;
        if (sep.length >= 4) {
            showParticles = Boolean.parseBoolean(sep[3]);
        }
        PotionEffect effect;
        if (potionEffectType != null) {
            effect = new PotionEffect(potionEffectType, duration, level, false, showParticles);
            player.addPotionEffect(effect);
        }
    }

    /**
     * Elimina un efecto de poción específico del jugador.
     * <p>
     * El tipo de efecto debe proporcionarse como una cadena (por ejemplo, {@code "SPEED"}, {@code "INVISIBILITY"}).
     *
     * @param player      Jugador al que se le eliminará el efecto.
     * @param actionLine  Nombre del efecto de poción a eliminar (no distingue entre mayúsculas y minúsculas).
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>{@code
     * utilsManagers.removePotionEffect(player, "SPEED");
     * }</pre>
     */
    public void removePotionEffect(Player player, String actionLine) {
        PotionEffectType potionEffectType = PotionEffectType.getByName(actionLine);
        if (potionEffectType != null) {
            player.removePotionEffect(potionEffectType);
        }
    }

    // messages
    public void sendCenteredMessage(Player player, String message) {
        if (message == null || message.isEmpty()) return;
        String coloredMessage = setPlaceholders(player, message);
        String centeredMessage = getCenteredMessage(coloredMessage);
        player.sendMessage(centeredMessage);
    }

    public void sendCenteredMessage(Player player, String... messages) {
        this.sendCenteredMessage(player, List.of(messages));
    }

    public void sendCenteredMessage(Player player, List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        for (String message : messages) {
            sendCenteredMessage(player, message);
        }
    }

    public void sendMessage(CommandSender sender, String message) {
        this.message(sender, message);
    }

    public void sendMessage(CommandSender sender, String... messages) {
        this.sendMessage(sender, List.of(messages));
    }

    public void sendMessage(CommandSender sender, List<String> messages) {
        if (messages == null) return;
        for (String message : messages) {
            this.message(sender, message);
        }
    }

    private void message(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        if (sender instanceof Player player) {
            player.sendMessage(setPlaceholders(player, message));
        } else {
            sender.sendMessage(setColoredMessage(message.replace("%prefix%", getPrefix())));
        }
    }

    public void sendBroadcastMessage(String... messages) {
        if (messages == null) return;
        for (String message : messages) {
            Bukkit.getOnlinePlayers().forEach(player -> sendMessage(player, message));
        }
    }

    public void sendBroadcastMessage(List<String> messages) {
        this.sendBroadcastMessage(messages.toArray(new String[0]));
    }

    public void sendBroadcastMessageCenter(String... messages) {
        if (messages == null) return;
        for (String message : messages) {
            Bukkit.getOnlinePlayers().forEach(player -> sendCenteredMessage(player, message));
        }
    }

    public void sendBroadcastMessageCenter(List<String> messages) {
       this.sendBroadcastMessageCenter(messages.toArray(new String[0]));
    }

    public void sendConsoleMessage(String... messages) {
        if (messages == null) return;
        for (String message : messages) {
            Bukkit.getConsoleSender().sendMessage(setColoredMessage(message));
        }
    }

    public void sendConsoleMessage(List<String> messages) {
        this.sendConsoleMessage(messages.toArray(new String[0]));
    }

    @Deprecated(since = "1.0.2")
    public void sendSpigotSendMessage(Player player, String message){
        if (message == null || message.isEmpty()) return;
        TextComponent mainComponent = new TextComponent(TextComponent.fromLegacyText(setPlaceholders(player, message)));
        player.spigot().sendMessage(mainComponent);
    }

    @Deprecated(since = "1.0.2")
    public void sendSpigotSendMessage(Player player, TextComponent mainComponent){
        if (mainComponent == null) return;
        player.spigot().sendMessage(mainComponent);
    }

    public void giveItem(Player player, ItemStack itemStack) {
        Map<Integer, ItemStack> stackHashMap = player.getInventory().addItem(itemStack);

        if (!stackHashMap.isEmpty()) {
            for (ItemStack leftover : stackHashMap.values()) {
                player.getWorld().dropItem(player.getLocation(), leftover);
            }
        }
    }

    /**
     * Kicks a player with a colored message.
     *
     * @param player  Player to kick
     * @param messages Message to send on kick
     */
    public void kickPlayer(Player player, String... messages) {
        if (messages == null || messages.length == 0) return;
        String message = String.join("\n", messages);
        player.kickPlayer(setColoredMessage(message));
    }

    public void kickPlayer(Player player, List<String> messages) {
        this.kickPlayer(player, messages.toArray(new String[0]));
    }

    // other
    @Deprecated(since = "1.0.0")
    public HoverEvent createHoverEvent(Player player, @NotNull List<String> hoverText) {
        TextComponent hoverComponent = new TextComponent("");
        boolean first = true;

        for (String line : hoverText) {
            if (!first) {
                hoverComponent.addExtra("\n");
            } else {
                first = false;
            }

            String replacedLine = setPlaceholders(player, line);
            hoverComponent.addExtra(new TextComponent(TextComponent.fromLegacyText(replacedLine)));
        }

        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverComponent).create());
    }

    @Deprecated(since = "1.0.0")
    public void applyClickAction(TextComponent component, @NotNull String clickAction, String playerName) {
        String replacedAction = clickAction.replace("%player%", playerName);
        replacedAction = setColoredMessage(replacedAction);
        if (replacedAction.startsWith("[EXECUTE]")) {
            String command = replacedAction.substring("[EXECUTE]".length()).trim();
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        } else if (replacedAction.startsWith("[OPEN]")) {
            String url = replacedAction.substring("[OPEN] ".length()).trim();
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        } else if (replacedAction.startsWith("[SUGGEST]")) {
            String command = replacedAction.substring("[SUGGEST] ".length()).trim();
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        }
    }

    @Deprecated(since = "1.0.0") @SuppressWarnings("removal")
    public TextComponent getNameComponent(Object value) {
        switch (value) {
            case ItemStack itemStack -> {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null && itemMeta.hasDisplayName()) {
                    return new TextComponent(itemMeta.getDisplayName());
                } else {
                    return new TextComponent(new TranslatableComponent(itemStack.getType().getTranslationKey()));
                }
            }
            case Entity entity -> {
                if (entity.getCustomName() != null) {
                    return new TextComponent(entity.getCustomName());
                } else {
                    return new TextComponent(new TranslatableComponent(entity.getType().getTranslationKey()));
                }
            }
            case Block block -> {
                return new TextComponent(new TranslatableComponent(block.getType().getTranslationKey()));
            }
            default -> {}
        }

        return null;
    }

    public void sendToServer(Player player,String actionLine) {
        ExApi.getBungeeMessagingManager().sendToServer(player, actionLine);
    }

    public void sendKnockback(Player player, String message) {
        Vector knockback = player.getLocation().getDirection().multiply(-1).setY(0.5);
        player.setVelocity(knockback);
        if (message != null) {
            sendMessage(player, "%prefix% " + message);
        }
    }

    @ToUse(
            value = "Ejecuta una lista de acciones sobre un jugador. Utilizado para acciones personalizadas mediante etiquetas.",
            params = {
                    "player: Jugador objetivo",
                    "actions: Lista de acciones a ejecutar"
            },
            usedFor = "Ejecución de acciones personalizadas",
            notes = {
                    "ACCIONES DISPONIBLES:",
                    "- [wait] time – Retraso antes de ejecutar las siguientes acciones.",
                    "- [teleport] world;x;y;z;yaw;pitch – Teletransporta al jugador.",
                    "- [title] title;subtitle;fadeIn;stay;fadeOut – Muestra título y subtítulo.",
                    "- [message] mensaje – Envía mensaje en chat.",
                    "- [message_center] mensaje – Envía mensaje centrado en chat.",
                    "- [action_bar] mensaje – Envía mensaje en barra de acción.",
                    "- [sound] sound;volume;pitch – Reproduce un sonido.",
                    "- [console] comando – Ejecuta comando desde consola.",
                    "- [player_command] comando – Ejecuta comando como jugador.",
                    "- [effect_add] tipo;duración;potencia – Aplica efecto de poción.",
                    "- [effect_remove] tipo – Quita efecto de poción.",
                    "- [effect_clear] – Quita todos los efectos.",
                    "- [give_item] material;cantidad;(opcional[nombre]) – Da un ítem.",
                    "- [broadcast] mensaje – Envía mensaje a todos.",
                    "- [kill] – Mata al jugador.",
                    "- [damage] cantidad – Daña al jugador.",
                    "- [heal] (opcional[cantidad]) – Cura al jugador.",
                    "- [feed] (opcional[cantidad]) – Alimenta y cura al jugador.",
                    "- [extinguish] – Apaga fuego.",
                    "- [fly] true|false – Activa/desactiva vuelo.",
                    "- [gamemode] modo – Cambia modo de juego.",
                    "- [clear_armor] – Elimina armadura.",
                    "- [launch] fuerza – Lanza al jugador.",
                    "- [velocity] x;y;z – Aplica velocidad.",
                    "- [freeze] (opcional[ticks]) – Congela jugador.",
                    "- [fire] (opcional[ticks]) – Prende fuego.",
                    "- [clear_inventory] – Borra inventario.",
                    "- [close_inventory] – Cierra inventario.",
                    "- [refresh_inventory] – Recarga menú (plugin).",
                    "- [inventory_first-page], [inventory_last-page], [inventory_prev-page], [inventory_next-page] – Control de paginación.",
                    "CONDICIONES DISPONIBLES:",
                    "- [has_item] material:cantidad -> acción – Ejecuta acción si tiene ítem.",
                    "- [has_permission] permiso -> acción – Ejecuta si tiene permiso.",
                    "- [has_world] mundo -> acción – Ejecuta en mundo específico.",
                    "- [has_placeholder] placeholder operador valor -> acción – Compara placeholder (requiere PAPI).",
                    "- [to_world] mundo -> acción – Ejecuta en jugadores de un mundo.",
                    "- [to_targets] jugador1;jugador2 -> acción – Ejecuta en jugadores específicos.",
                    "- [to_range] radio;incluirJugador -> acción – Ejecuta en radio desde jugador.",
                    "VARIABLES:",
                    "Puedes usar placeholders PAPI; %player% es el jugador actual."
            }
    )
    public void executeActions(Player player, List<String> actions) {
        new Action(this).executeActions(player, actions);
    }
}