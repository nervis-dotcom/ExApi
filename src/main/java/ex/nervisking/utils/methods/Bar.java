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

public abstract class Bar {

    private final JavaPlugin plugin;
    private final UtilsManagers utilsManagers;
    private BossBar bossBar;
    private BukkitRunnable task;

    private long endTime = 0;
    private long duration = 0;

    public Bar() {
        this.plugin = ExApi.getPlugin();
        this.utilsManagers = ExApi.getUtilsManagers();
    }

    public abstract BarColor getBarColor();
    public abstract BarStyle getBarStyle();
    public abstract String getTitle();

    public void startBar(long seconds) {
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar(" ", getBarColor(), getBarStyle());
            bossBar.setVisible(true);
        }

        if (task != null) {
            task.cancel();
        }

        this.duration = seconds;
        this.endTime = System.currentTimeMillis() + (seconds * 1000);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                long timeLeft = getTimeLeft();

                if (timeLeft > 0 && !bossBar.getPlayers().isEmpty()) {
                    bossBar.setTitle(utilsManagers.setColoredMessage(getTitle() + " &7(" + timeLeft + "s)"));
                    bossBar.setProgress(Math.max(0, timeLeft / (double) duration));
                } else {
                    stop();
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }

    public void addPlayer(Player player) {
        if (bossBar == null || getTimeLeft() <= 0) {
            return;
        }
        if (!bossBar.getPlayers().contains(player)) {
            bossBar.addPlayer(player);
        }
    }

    public void removePlayer(Player player) {
        if (bossBar != null) {
            bossBar.removePlayer(player);
            if (bossBar.getPlayers().isEmpty()) {
                stop();
            }
        }
    }

    public void stop() {
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            bossBar = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        endTime = 0;
        duration = 0;
    }

    public long getTimeLeft() {
        long millisLeft = endTime - System.currentTimeMillis();
        return Math.max(0, millisLeft / 1000);
    }

    public void addTime(long seconds) {
        this.endTime += seconds * 1000;
        this.duration = Math.max(duration, getTimeLeft());
    }
}
