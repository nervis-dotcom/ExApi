package ex.nervisking.utils.methods;

import ex.nervisking.ExApi;
import ex.nervisking.utils.UtilsManagers;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerBar {

    private final JavaPlugin plugin = ExApi.getPlugin();
    private final UtilsManagers utilsManagers = ExApi.getUtilsManagers();

    private final Player player;
    private final BossBar bossBar;
    private BukkitRunnable task;

    private int timeLeft;
    private int totalTime;

    public PlayerBar(Player player, int duration, BarColor color, BarStyle style, String title) {
        this.player = player;
        this.timeLeft = duration;
        this.totalTime = duration;

        this.bossBar = Bukkit.createBossBar(utilsManagers.setColoredMessage(title + " &7(" + timeLeft + "s)"), color, style);
        this.bossBar.addPlayer(player);
        this.bossBar.setVisible(true);

        startTimer(title);
    }

    private void startTimer(String title) {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (timeLeft <= 0 || !player.isOnline()) {
                    remove();
                    return;
                }
                bossBar.setProgress(timeLeft / (double) totalTime);
                bossBar.setTitle(utilsManagers.setColoredMessage(title + " &7(" + timeLeft + "s)"));
                timeLeft--;
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }

    public void addTime(int seconds) {
        this.timeLeft += seconds;
        this.totalTime = Math.max(this.totalTime, this.timeLeft);
    }

    public void remove() {
        if (task != null) task.cancel();
        bossBar.removeAll();
    }

    public int getTimeLeft() {
        return timeLeft;
    }
}
