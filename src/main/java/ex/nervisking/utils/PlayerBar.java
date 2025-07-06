package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Logger;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @since 1.1.0
 */
public class PlayerBar {

    private final UtilsManagers utilsManagers = ExApi.getUtilsManagers();
    private BossBar bossBar;
    private final Set<Player> players;
    private final Set<BarFlag> barFlags;
    private BukkitRunnable task;
    private boolean running;
    private Runnable onFinish;
    private long initialTime = -1;
    private BarColor color;
    private BarStyle style;

    private long timeLeft;
    private long totalTime;
    private String title;

    public PlayerBar() {
        this.running = false;
        this.players = new HashSet<>();
        this.barFlags = new HashSet<>();
        this.title = " ";
        this.color = BarColor.WHITE;
        this.style = BarStyle.SOLID;
        this.timeLeft = 0L;
        this.totalTime = 0L;
        this.createBossBar();
    }

    public PlayerBar(String title) {
        this.running = false;
        this.players = new HashSet<>();
        this.barFlags = new HashSet<>();
        this.title = title;
        this.color = BarColor.WHITE;
        this.style = BarStyle.SOLID;
        this.timeLeft = 0L;
        this.totalTime = 0L;
        this.createBossBar();
    }

    public PlayerBar(String title, BarColor barColor) {
        this.running = false;
        this.players = new HashSet<>();
        this.barFlags = new HashSet<>();
        this.title = title;
        this.color = barColor;
        this.style = BarStyle.SOLID;
        this.timeLeft = 0L;
        this.totalTime = 0L;
        this.createBossBar();
    }

    public PlayerBar(String title, BarStyle barStyle) {
        this.running = false;
        this.players = new HashSet<>();
        this.barFlags = new HashSet<>();
        this.title = title;
        this.color = BarColor.WHITE;
        this.style = barStyle;
        this.timeLeft = 0L;
        this.totalTime = 0L;
        this.createBossBar();
    }

    public PlayerBar(String title, BarColor barColor, BarStyle barStyle) {
        this.running = false;
        this.players = new HashSet<>();
        this.barFlags = new HashSet<>();
        this.title = title;
        this.color = barColor;
        this.style = barStyle;
        this.timeLeft = 0L;
        this.totalTime = 0L;
        this.createBossBar();
    }

    public PlayerBar(String title, BarColor barColor, BarStyle barStyle, BarFlag... flag) {
        this.running = false;
        this.players = new HashSet<>();
        this.barFlags = new HashSet<>();
        this.title = title;
        this.color = barColor;
        this.style = barStyle;
        this.barFlags.addAll(Arrays.asList(flag));
        this.timeLeft = 0L;
        this.totalTime = 0L;
        this.createBossBar();
    }

    public PlayerBar start() {
        if (running) {
            utilsManagers.sendLogger(Logger.WARNING, "Ya hay una bossbar activa. No se puede iniciar otra.", true);
            return this;
        }

        if (timeLeft <= 0) {
            utilsManagers.sendLogger(Logger.WARNING, "No se puede iniciar la bossbar sin haber asignado un tiempo.", true);
            return this;
        }

        if (players.isEmpty()) {
            utilsManagers.sendLogger(Logger.WARNING, "No se puede iniciar la bossbar sin jugadores asignados.", true);
            return this;
        }

        this.running = true;
        this.totalTime = timeLeft;
        this.initialTime = timeLeft; // 🔸 Guarda el tiempo inicial
        startTimer();
        return this;
    }

    private boolean hasOnlinePlayers() {
        return players.stream().anyMatch(Player::isOnline);
    }

    public PlayerBar onFinish(Runnable runnable) {
        this.onFinish = runnable;
        return this;
    }

    public PlayerBar setVisibleAll(boolean status) {
        this.bossBar.setVisible(status);
        return this;
    }

    public PlayerBar addPlayers(Set<Player> newPlayers) {
        this.players.addAll(newPlayers);
        for (Player player : newPlayers) {
            bossBar.addPlayer(player);
        }
        return this;
    }

    public PlayerBar addPlayer(Player player) {
        this.players.add(player);
        bossBar.addPlayer(player);
        return this;
    }

    public PlayerBar removePlayer(Player player) {
        this.players.remove(player);
        this.bossBar.removePlayer(player);
        return this;
    }

    public PlayerBar addTime(String seconds) {
        long millis;
        try {
            millis = utilsManagers.parseTime(seconds);
        } catch (NumberFormatException e) {
            millis = 10 * 1000L;
        }
        this.timeLeft += millis;
        this.totalTime = Math.max(this.totalTime, this.timeLeft);
        return this;
    }

    public PlayerBar setTitle(String title) {
        this.title = title;
        this.bossBar.setTitle(utilsManagers.setColoredMessage(title));
        return this;
    }

    public PlayerBar setColor(BarColor color) {
        this.color = color;
        this.bossBar.setColor(color);
        return this;
    }

    public PlayerBar setStyle(BarStyle style) {
        this.style = style;
        this.bossBar.setStyle(style);
        return this;
    }

    public PlayerBar setColor(String color) {
        BarColor barColor;
        try {
            barColor = BarColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            barColor = BarColor.WHITE;
            this.utilsManagers.sendLogger(Logger.WARNING, "Color de la bossbar no es correcto: " + color);
        }
        this.color = barColor;
        return setColor(barColor);
    }

    public PlayerBar setStyle(String style) {
        BarStyle barStyle;
        try {
            barStyle = BarStyle.valueOf(style.toUpperCase());
        } catch (IllegalArgumentException e) {
            barStyle = BarStyle.SOLID;
            this.utilsManagers.sendLogger(Logger.WARNING, "Estilo de la bossbar no es correcto: " + style);
        }
        this.style = barStyle;
        return setStyle(barStyle);
    }

    public PlayerBar addFlag(BarFlag flag) {
        this.barFlags.add(flag);
        if (bossBar != null) {
            this.bossBar.addFlag(flag);
        }
        return this;
    }

    public PlayerBar removeFlag(BarFlag flag) {
        this.barFlags.remove(flag);
        if (bossBar != null) {
            this.bossBar.removeFlag(flag);
        }
        return this;
    }

    public PlayerBar clearFlags() {
        this.barFlags.clear();
        if (bossBar != null) {
            for (BarFlag flag : BarFlag.values()) {
                this.bossBar.removeFlag(flag);
            }
        }
        return this;
    }

    private void createBossBar() {
        if (bossBar != null) {
            this.bossBar.removeAll();
        }
        // Usa los flags configurados:
        this.bossBar = Bukkit.createBossBar(title, color, style, barFlags.toArray(new BarFlag[0]));
        this. bossBar.setVisible(true);
        // Re-agrega los jugadores actuales
        for (Player p : players) {
            this.bossBar.addPlayer(p);
        }
    }

    public void remove() {
        if (task != null) {
            task.cancel();
        }
        this.bossBar.removeAll();
        this.players.clear();
    }

    public void stop() {
        if (task != null) {
            this.task.cancel();
        }
        this.clearPlayers();
        this.bossBar = null;
        this.running = false;
    }

    public void clearPlayers() {
        this.bossBar.removeAll();
        this.bossBar.setVisible(false);
        this.players.clear();
    }

    public boolean iaVisible() {
        return this.bossBar.isVisible();
    }

    public void pause() {
        if (!running || task == null) {
            this.utilsManagers.sendLogger(Logger.INFO, "La bossbar no está activa. No se puede pausar.", true);
            return;
        }

        this.task.cancel();
        this.task = null;
        this.running = false;
        this.utilsManagers.sendLogger(Logger.INFO, "La bossbar ha sido pausada.", true);
    }

    public void resume() {
        if (running) {
            this.utilsManagers.sendLogger(Logger.WARNING, "La bossbar ya está activa. No se puede reanudar.", true);
            return;
        }

        if (timeLeft <= 0) {
            this.utilsManagers.sendLogger(Logger.WARNING, "No se puede reanudar la bossbar: no queda tiempo.", true);
            return;
        }

        if (players.isEmpty()) {
            this.utilsManagers.sendLogger(Logger.WARNING, "No se puede reanudar la bossbar sin jugadores asignados.", true);
            return;
        }

        this.running = true;
        this.startTimer();
        this.utilsManagers.sendLogger(Logger.INFO, "La bossbar ha sido reanudada.", true);
    }

    public void restart() {
        if (players.isEmpty()) {
            this.utilsManagers.sendLogger(Logger.WARNING, "No se puede reiniciar la bossbar sin jugadores asignados.", true);
            return;
        }

        if (initialTime <= 0) {
            this.utilsManagers.sendLogger(Logger.WARNING, "No se puede reiniciar la bossbar: no se ha definido un tiempo inicial.", true);
            return;
        }

        if (task != null) {
            this.task.cancel();
        }

        this.timeLeft = initialTime;
        this.totalTime = initialTime;
        this.running = true;
        this.startTimer();
        this.utilsManagers.sendLogger(Logger.INFO, "La bossbar ha sido reiniciada desde " + initialTime + " segundos.", true);
    }

    private void startTimer() {
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (timeLeft <= 0 || !hasOnlinePlayers()) {
                    if (onFinish != null) onFinish.run();
                    remove();
                    return;
                }

                double progress = Math.max(0, Math.min(1, timeLeft / (double) totalTime));
                bossBar.setProgress(progress);
                bossBar.setTitle(utilsManagers.setColoredMessage(title));
                timeLeft -= 1000;
            }
        };
        this.task.runTaskTimer(ExApi.getPlugin(), 0L, 20L);
    }

    public long getTimeLeft() {
        return this.timeLeft;
    }

    public long getTotalTime() {
        return this.totalTime;
    }

    public BossBar getBossBar() {
        return this.bossBar;
    }

    public boolean isRunning() {
        return this.running;
    }

    public BukkitRunnable getTask() {
        return this.task;
    }

    public Set<Player> getPlayers() {
        return this.players;
    }
}