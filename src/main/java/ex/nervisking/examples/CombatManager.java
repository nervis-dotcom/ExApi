package ex.nervisking.examples;

import ex.nervisking.utils.methods.PlayerBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private final Map<UUID, PlayerBar> combatBars = new HashMap<>();

    public void enterCombat(Player player, int durationSeconds) {
        UUID uuid = player.getUniqueId();

        if (combatBars.containsKey(uuid)) {
            combatBars.get(uuid).addTime(durationSeconds);
        } else {
            PlayerBar bar = new PlayerBar(player, durationSeconds, BarColor.RED, BarStyle.SEGMENTED_6, "&c¡Estás en combate!");
            combatBars.put(uuid, bar);
        }
    }

    public boolean isInCombat(Player player) {
        return combatBars.containsKey(player.getUniqueId());
    }

    public void leaveCombat(Player player) {
        UUID uuid = player.getUniqueId();
        if (combatBars.containsKey(uuid)) {
            combatBars.get(uuid).remove();
            combatBars.remove(uuid);
        }
    }
}
