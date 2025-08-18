package ex.nervisking.menuManager;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Pattern.ToUse;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class MenuUser {

    private final Player player;
    private final UtilsManagers utilsManagers;

    public MenuUser(@NotNull Player player) {
        this.player = player;
        this.utilsManagers = ExApi.getUtilsManagers();
    }

    @ToUse("Obtener el objeto Player original")
    public Player getPlayer() {
        return player;
    }

    @ToUse("Verificar si el target es el mismo jugador")
    public boolean is(Player target) {
        return player.equals(target);
    }

    @ToUse("Verificar si el jugador es OP o tiene permisos administrativos")
    public boolean isOp() {
        return player.isOp() || utilsManagers.hasOp(player);
    }

    @ToUse("Obtener el nombre del jugador")
    public String getName() {
        return player.getName();
    }

    @ToUse("Obtener la ubicación actual del jugador")
    public Location getLocation() {
        return player.getLocation();
    }

    @ToUse("Obtener el inventario del jugador")
    public PlayerInventory getInventory() {
        return player.getInventory();
    }

    @ToUse("Obtener el mundo donde se encuentra el jugador")
    public World getWorld() {
        return player.getWorld();
    }

    @ToUse("Obtener la UUID única del jugador")
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    // ---------------- MENSAJES ----------------

    @ToUse("Enviar un mensaje con soporte de colores (§) al jugador")
    public void sendMessage(@NotNull String message) {
        utilsManagers.sendMessage(player, message);
    }

    @ToUse("Enviar múltiples mensajes desde una lista al jugador")
    public void sendMessages(@NotNull List<String> messages) {
        utilsManagers.sendMessage(player, messages);
    }

    @ToUse("Enviar múltiples mensajes usando varargs al jugador")
    public void sendMessages(@NotNull String... messages) {
        utilsManagers.sendMessage(player, messages);
    }

    // ---------------- SONIDOS ----------------

    @ToUse("Reproducir un sonido personalizado en la ubicación del jugador")
    public void playSound(@NotNull Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    @ToUse("Reproducir un sonido usando su nombre en formato String")
    public void playSound(@NotNull String sound) {
        utilsManagers.playSound(player, sound);
    }

    // ---------------- TELETRANSPORTE ----------------

    @ToUse("Teletransportar al jugador a una ubicación específica")
    public void teleport(@NotNull Location location) {
        player.teleport(location);
    }

    // ---------------- PERMISOS ----------------

    @ToUse("Comprobar si el jugador tiene un permiso específico")
    public boolean hasPermission(@NotNull String perm) {
        return utilsManagers.hasPermission(player, perm);
    }

    // ---------------- TÍTULOS Y ACTION BAR ----------------

    @ToUse("Enviar un título y subtítulo al jugador con tiempos de fadeIn, stay y fadeOut")
    public void sendTitle(@NotNull String title, @NotNull String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(utilsManagers.setPlaceholders(player, title),
                utilsManagers.setPlaceholders(player, subtitle),
                fadeIn, stay, fadeOut);
    }

    @ToUse("Enviar un título simple al jugador (sin subtítulo)")
    public void sendTitle(@NotNull String title) {
        utilsManagers.sendTitle(player, title);
    }

    @ToUse("Enviar un mensaje en la ActionBar del jugador")
    public void sendActionBar(@NotNull String message) {
        utilsManagers.sendActionBar(player, message);
    }

    // ---------------- ACCIONES ----------------

    @ToUse("Ejecutar una lista de acciones definidas para el jugador (ej: comandos, scripts)")
    public void executeActions(@NotNull List<String> actions) {
        utilsManagers.executeActions(player, actions);
    }
}
