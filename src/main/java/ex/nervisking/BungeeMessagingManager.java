package ex.nervisking;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public record BungeeMessagingManager(JavaPlugin plugin) implements PluginMessageListener {

    public BungeeMessagingManager(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte @NotNull [] bytes) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
    }

    public void sendToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}