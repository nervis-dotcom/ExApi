package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Logger;
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
import java.util.Objects;

@SuppressWarnings("deprecation")
public class UtilsManagers extends Utils {

    private final JavaPlugin plugin;
    private final HashMap<String, Long> cooldowns = new HashMap<>();

    public UtilsManagers() {
        this.plugin = ExApi.getPlugin();
    }

    public boolean getCooldown(String string){
        if (cooldowns.containsKey(string)) {
            long secondsLeft = (cooldowns.get(string) + 500) - System.currentTimeMillis();
            if (secondsLeft > 0) {
                return true;
            }
        }

        cooldowns.put(string, System.currentTimeMillis());
        return false;
    }

    public boolean hasPermission(CommandSender sender, String permission){
        return sender.hasPermission(plugin.getName().toLowerCase() + "." + permission);
    }
    public boolean hasOp(CommandSender sender){
        return sender.hasPermission(plugin.getName().toLowerCase() + ".owner");
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
            sendLogger(Logger.ERROR,"se necesitan minimo 3 partes para reproducir un sonido");
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
            sendLogger(Logger.WARNING,"Nombre del sonido: &c" + sep[0] + " no es válido.");
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
        int fadeIn = Integer.parseInt(sep[2]);
        int stay = Integer.parseInt(sep[3]);
        int fadeOut = Integer.parseInt(sep[4]);

        String title = sep[0];
        String subtitle = sep[1];
        if (title.equals("none")) {
            title = "";
        }
        if (subtitle.equals("none")) {
            subtitle = "";
        }

        title = setPlaceholders(player, title);
        subtitle = setPlaceholders(player, subtitle);
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
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
    public void actionBar(Player player, String message) {
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
        actionBar(player, message);

        if (duration > 0) {
            // Envía un mensaje vacío al final del tiempo límite. Permite mensajes de menos de 3 segundos para garantizar la precisión.
            new BukkitRunnable() {
                @Override
                public void run() {
                    actionBar(player, "");
                }
            }.runTaskLater(plugin, duration + 1);
        }

        // Reenvía los mensajes cada 3 segundos, para que no desaparezcan de la pantalla del reproductor.
        while (duration > 40) {
            duration -= 40;
            new BukkitRunnable() {
                @Override
                public void run() {
                    actionBar(player, message);
                }
            }.runTaskLater(plugin, duration);
        }
    }

    /**
     * Ejecuta un comando en consola con soporte para placeholders.
     * <p>
     * Los placeholders compatibles se reemplazarán usando el contexto del jugador antes de ejecutar el comando.
     *
     * @param player  Jugador cuyo contexto se usará para reemplazar los placeholders.
     * @param commands Comando a ejecutar (puede incluir placeholders como {@code %player_name%}, {@code %vault_eco_balance%}, etc.)
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>{@code
     * utilsManagers.sendConsoleCommand(player, "say %player_name% ha usado un ítem especial");
     * }</pre>
     */

    public void sendConsoleCommand(Player player, String... commands) {
        if (commands == null) return;
        for (String cmd : commands) {
            sendConsoleCommand(player, cmd);
        }
    }
    public void sendConsoleCommand(Player player, List<String> commands) {
        if (commands == null) return;
        for (String cmd : commands) {
            sendConsoleCommand(player, cmd);
        }
    }

    public void sendConsoleCommand(Player player, String command) {
        if (command == null || command.isEmpty()) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), setPlaceholders(player, command));
    }

    /**
     * Hace que el jugador ejecute un comando con soporte para placeholders.
     * <p>
     * Los placeholders compatibles se reemplazarán usando el contexto del jugador antes de ejecutar el comando.
     *
     * @param player  Jugador que ejecutará el comando.
     * @param command Comando a ejecutar (puede incluir placeholders como {@code %player_name%}, {@code %vault_eco_balance%}, etc.)
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>{@code
     * utilsManagers.sendPerformCommand(player, "warp %player_name%");
     * }</pre>
     */
    public void sendPerformCommand(Player player, Object command) {
        switch (command) {
            case String string -> sendPerformCommand(player, string);
            case List<?> list -> {
                for (Object msg : list) {
                    if (msg instanceof String m) {
                        sendPerformCommand(player, m);
                    }
                }
            }
            case String[] array -> {
                for (String msg : array) {
                    sendPerformCommand(player, msg);
                }
            }
            case null, default -> {}
        }
    }

    private void sendPerformCommand(Player player, String command) {
        if (command == null || command.isEmpty()) return;
        player.performCommand(setPlaceholders(player, command));
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
                sendLogger(Logger.WARNING,"Nombre de la partícula: " + effectName + " no es válido.");
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
            if (sep.length > 6) {
                World world = Bukkit.getWorld(sep[0]);
                double x = Double.parseDouble(sep[1]);
                double y = Double.parseDouble(sep[2]);
                double z = Double.parseDouble(sep[3]);
                float yaw = Float.parseFloat(sep[4]);
                float pitch = Float.parseFloat(sep[5]);
                Location l = new Location(world, x, y, z, yaw, pitch);
                player.teleport(l);
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
    public void centeredMessage(Player player, String message) {
        if (message == null || message.isEmpty()) return;
        String coloredMessage = setPlaceholders(player, message);
        String centeredMessage = getCenteredMessage(coloredMessage);
        player.sendMessage(centeredMessage);
    }

    public void centeredMessage(Player player, String... messages) {
        if (messages == null) return;
        for (String message : messages) {
            centeredMessage(player, message);
        }
    }

    public void centeredMessage(Player player, List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        for (String message : messages) {
            centeredMessage(player, message);
        }
    }

    public void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        if (sender instanceof Player player) {
            player.sendMessage(setPlaceholders(player, message));
        } else {
            sender.sendMessage(setColoredMessage(message.replace("%prefix%", getPrefix())));
        }
    }

    public void sendMessage(CommandSender sender, String... messages) {
        if (messages == null) return;
        for (String message : messages) {
            sendMessage(sender, message);
        }
    }

    public void sendMessage(CommandSender sender, List<String> messages) {
        if (messages == null) return;
        for (String message : messages) {
            sendMessage(sender, message);
        }
    }

    public void broadcastMessage(String message) {
        if (message == null || message.isEmpty()) return;
        sendSingleBroadcastMessage(message);
    }

    public void broadcastMessage(String... messages) {
        if (messages == null) return;
        for (String message : messages) {
            sendSingleBroadcastMessage(message);
        }
    }

    public void broadcastMessage(List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        for (String message : messages) {
            sendSingleBroadcastMessage(message);
        }
    }

    private void sendSingleBroadcastMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> sendMessage(player, message));
    }

    public void broadcastMessageCenter(String message) {
        if (message == null || message.isEmpty()) return;
        sendSingleBroadcastMessageCenter(message);
    }

    public void broadcastMessageCenter(String... messages) {
        if (messages == null) return;
        for (String message : messages) {
            sendSingleBroadcastMessageCenter(message);
        }
    }

    public void broadcastMessageCenter(List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        for (String message : messages) {
            sendSingleBroadcastMessageCenter(message);
        }
    }

    private void sendSingleBroadcastMessageCenter(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> centeredMessage(player, message));
    }

    public void consoleMessage(String message) {
        if (message == null || message.isEmpty()) return;
        sendSingleConsoleMessage(message);
    }

    public void consoleMessage(String... messages) {
        if (messages == null) return;
        for (String message : messages) {
            sendSingleConsoleMessage(message);
        }
    }

    public void consoleMessage(List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        for (String message : messages) {
            sendSingleConsoleMessage(message);
        }
    }

    private void sendSingleConsoleMessage(String message){
        if (message == null || message.isEmpty()) return;
        Bukkit.getConsoleSender().sendMessage(setColoredMessage(message));
    }

    public void sendSpigotSendMessage(Player player, String message){
        if (message == null || message.isEmpty()) return;
        TextComponent mainComponent = new TextComponent(TextComponent.fromLegacyText(setPlaceholders(player, message)));
        player.spigot().sendMessage(mainComponent);
    }

    public void sendSpigotSendMessage(Player player, TextComponent mainComponent){
        if (mainComponent == null) return;
        player.spigot().sendMessage(mainComponent);
    }

    /**
     * Kicks a player with a colored message.
     *
     * @param player  Player to kick
     * @param message Message to send on kick
     */
    public void kickPlayer(Player player, String message) {
        if (message == null || message.isEmpty()) return;
        player.kickPlayer(setColoredMessage(message));
    }

    public void kickPlayer(Player player, String... messages) {
        if (messages == null || messages.length == 0) return;
        String message = String.join("\n", messages);
        player.kickPlayer(setColoredMessage(message));
    }

    public void kickPlayer(Player player, List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        String message = String.join("\n", messages);
        player.kickPlayer(setColoredMessage(message));
    }

    /**
     * Envía un mensaje al log de la consola con el prefijo del plugin.
     */
    public void sendLogger(Logger logger, List<String> messages) {
        if (messages == null || messages.isEmpty()) return;

        consoleMessage(getPrefix());
        for (String message : messages) {
            consoleMessage(logger.getName() + message);
        }
    }

    public void sendLogger(Logger logger, String... messages) {
        if (messages == null || messages.length == 0) return;

        consoleMessage(getPrefix());
        for (String message : messages) {
            consoleMessage(logger.getName() + message);
        }
    }

    public void sendLogger(Logger logger, String message) {
        if (message == null || message.isEmpty()) return;
        consoleMessage(getPrefix() + logger.getName() + message);
    }

    // other

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

    @SuppressWarnings("removal")
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

    public void getKnockback(Player player, String message) {
        Vector knockback = player.getLocation().getDirection().multiply(-1).setY(0.5);
        player.setVelocity(knockback);
        sendMessage(player, "%prefix% " + message);
    }

    // actions

    /**
     * Ejecuta una lista de acciones sobre un jugador. Esta utilidad puede utilizarse en secciones del plugin que acepten
     * acciones personalizadas mediante etiquetas.
     *
     * <p><strong>ACCIONES DISPONIBLES:</strong></p>
     *
     * <ul>
     *     <li><b>[wait] time</b> – Aplica un retraso antes de ejecutar las siguientes acciones.</li>
     *     <li><b>[teleport] world;x;y;z;yaw;pitch</b> – Teletransporta al jugador a una ubicación específica.</li>
     *     <li><b>[title] title;subtitle;fadeIn;stay;fadeOut</b> – Muestra un título y subtítulo al jugador. <i>Ej:</i> "[title] Bienvenido;Jugador;10;20;10"</li>
     *     <li><b>[message] mensaje</b> – Envía un mensaje al jugador en el chat.</li>
     *     <li><b>[message_center] mensaje</b> – Envía un mensaje centrado en el chat.</li>
     *     <li><b>[action_bar] mensaje</b> – Envía un mensaje en la barra de acción.</li>
     *     <li><b>[sound] sound;volume;pitch</b> – Reproduce un sonido al jugador.</li>
     *     <li><b>[console] comando</b> – Ejecuta un comando desde la consola.</li>
     *     <li><b>[player_command] comando</b> – Ejecuta un comando como el jugador.</li>
     *     <li><b>[effect_add] tipo;duración;potencia</b> – Aplica un efecto de poción al jugador.</li>
     *     <li><b>[effect_remove] tipo</b> – quita el efecto de poción al jugador.</li>
     *     <li><b>[effect_clear] tipo</b> – quita todos los efectos de poción al jugador.</li>
     *     <li><b>[give_item] material;cantidad;(opcional[nombre])</b> – Da un ítem al jugador.</li>
     *     <li><b>[broadcast] mensaje</b> – Envía un mensaje a todos los jugadores.</li>
     *     <li><b>[kill]</b> – Mata al jugador.</li>
     *     <li><b>[damage] cantidad</b> – Daña al jugador.</li>
     *     <li><b>[heal] (opcional[cantidad])</b> – Cura al jugador.</li>
     *     <li><b>[feed] (opcional[cantidad])</b> – Alimenta al jugador (y lo cura).</li>
     *     <li><b>[extinguish]</b> – Apaga el fuego del jugador.</li>
     *     <li><b>[fly] true|false</b> – Activa o desactiva el modo vuelo.</li>
     *     <li><b>[gamemode] modo</b> – Cambia el modo de juego del jugador.</li>
     *     <li><b>[clear_armor]</b> – Elimina la armadura del jugador.</li>
     *     <li><b>[launch] fuerza</b> – Lanza al jugador hacia arriba.</li>
     *     <li><b>[velocity] x;y;z</b> – Aplica una velocidad específica al jugador.</li>
     *     <li><b>[freeze] (opcional[ticks])</b> – Congela al jugador.</li>
     *     <li><b>[fire] (opcional[ticks])</b> – Prende fuego al jugador.</li>
     *     <li><b>[clear_inventory]</b> – Borra el inventario del jugador.</li>
     *     <li><b>[close_inventory]</b> – Cierra el inventario del jugador.</li>
     *     <!-- Acciones específicas del sistema de menús -->
     *     <li><b>[refresh_inventory]</b> – Recarga el menú del plugin (solo para este plugin).</li>
     *     <li><b>[inventory_first-page]</b>, <b>[inventory_last-page]</b>, <b>[inventory_prev-page]</b>, <b>[inventory_next-page]</b> – Control de paginación en menús del plugin.</li>
     * </ul>
     *
     * <p><strong>CONDICIONES DISPONIBLES:</strong></p>
     *
     * <ul>
     *     <li><b>[has_item] material:cantidad -> acción</b> – Ejecuta la acción si el jugador tiene el ítem. Ej: "[has_item] DIAMOND:5 -> [message] Tienes 5 diamantes"</li>
     *     <li><b>[has_permission] permiso -> acción</b> – Ejecuta la acción si el jugador tiene el permiso.</li>
     *     <li><b>[has_world] mundo -> acción</b> – Ejecuta la acción si el jugador está en ese mundo.</li>
     *     <li><b>[has_placeholder] placeholder operador valor -> acción</b> – (Requiere PlaceholderAPI). Compara un placeholder con un valor. Ej:
     *         <ul>
     *             <li>"[has_placeholder] %player_health% &lt; 10 -> [message] ¡Te estás muriendo!"</li>
     *             <li>"[has_placeholder] %vault_eco_balance% &gt;= 10000 -> [message] Eres rico"</li>
     *             <li>"[has_placeholder] %player_name% == Steve -> [message] Hola Steve"</li>
     *         </ul>
     *     </li>
     *     <li><b>[to_world] mundo -> acción</b> – Ejecuta la acción en todos los jugadores del mundo indicado.</li>
     *     <li><b>[to_targets] jugador1;jugador2 -> acción</b> – Ejecuta la acción en jugadores específicos.</li>
     *     <li><b>[to_range] radio;incluirJugador -> acción</b> – Ejecuta la acción en un radio desde el jugador. Ej: "[to_range] 15;false -> [sound] sonido;2;1"</li>
     * </ul>
     *
     * <p><strong>VARIABLES:</strong></p>
     * Puedes utilizar placeholders de PlaceholderAPI (PAPI). Por defecto, <code>%player%</code> representa al jugador actual.
     *
     * @param player Jugador objetivo
     * @param actions Lista de acciones a ejecutar
     */
    public void executeActions(Player player, List<String> actions) {
        if (actions == null || actions.isEmpty()) return;
        Action action = new Action(this);
        action.executeActions(player, actions);
    }

}
