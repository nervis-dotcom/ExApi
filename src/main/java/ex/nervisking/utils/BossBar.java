package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Logger;
import ex.nervisking.ModelManager.Scheduler;
import ex.nervisking.ModelManager.Task;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @since 1.0.1
 */
public class BossBar {

    private final UtilsManagers utilsManagers = ExApi.getUtilsManagers();
    private final Set<UUID> players;
    private final Set<BarFlag> barFlags;
    private org.bukkit.boss.BossBar bossBar;
    private Task task;
    private boolean running;
    private Runnable onFinish;
    private BossBarTick onTick;
    private long initialTime = -1;
    private BarColor color;
    private BarStyle style;
    private long timeLeft;
    private long totalTime;
    private String title;
    private boolean deleteAnDeath;

    public BossBar() {
        this(" ");
    }

    public BossBar(String title) {
        this(title, BarColor.WHITE, BarStyle.SOLID);
    }

    public BossBar(String title, BarColor barColor) {
        this(title, barColor, BarStyle.SOLID);
    }

    public BossBar(String title, BarStyle barStyle) {
        this(title, BarColor.WHITE, barStyle);
    }

    public BossBar(String title, BarColor barColor, BarStyle barStyle) {
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

    @Contract(" -> new")
    public static @NotNull BossBar of() {
        return new BossBar();
    }

    @Contract("_ -> new")
    public static @NotNull BossBar of(String title) {
        return new BossBar(title);
    }

    @Contract("_, _ -> new")
    public static @NotNull BossBar of(String title, BarColor barColor) {
        return new BossBar(title, barColor);
    }

    @Contract("_, _ -> new")
    public static @NotNull BossBar of(String title, BarStyle barStyle) {
        return new BossBar(title, barStyle);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull BossBar of(String title, BarColor barColor, BarStyle barStyle) {
        return new BossBar(title, barColor, barStyle);
    }

    public BossBar onFinish(Runnable runnable) {
        this.onFinish = runnable;
        return this;
    }

    public BossBar setVisibleAll(boolean status) {
        this.bossBar.setVisible(status);
        return this;
    }

    public BossBar addPlayers(Set<Player> newPlayers) {
        for (Player player : newPlayers) {
            UUID uuid = player.getUniqueId();
            players.add(uuid);
            bossBar.addPlayer(player);
        }
        return this;
    }

    public BossBar addPlayer(Player player) {
        this.players.add(player.getUniqueId());
        bossBar.addPlayer(player);
        return this;
    }

    public boolean hasPlayer(Player player) {
        return this.players.contains(player.getUniqueId());
    }

    public boolean hasPlayer(UUID uuid) {
        return this.players.contains(uuid);
    }

    public BossBar removePlayer(Player player) {
        this.players.remove(player.getUniqueId());
        this.bossBar.removePlayer(player);
        return this;
    }

    public BossBar addTime(String seconds) {
        long millis;
        try {
            millis = utilsManagers.parseTime(seconds);
        } catch (NumberFormatException e) {
            millis = 1000L;
        }
        this.timeLeft += millis;
        this.totalTime = Math.max(this.totalTime, this.timeLeft);
        return this;
    }

    public BossBar setTitle(String title) {
        this.title = title;
        this.bossBar.setTitle(utilsManagers.setColoredMessage(title));
        return this;
    }

    public BossBar setColor(BarColor color) {
        this.color = color;
        this.bossBar.setColor(color);
        return this;
    }

    public BossBar setStyle(BarStyle style) {
        this.style = style;
        this.bossBar.setStyle(style);
        return this;
    }

    public BossBar setColor(String color) {
        BarColor barColor;
        try {
            barColor = BarColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            barColor = BarColor.WHITE;
            ExLog.sendWarning("Color de la bossbar no es correcto: " + color);
        }
        return setColor(barColor);
    }

    public BossBar setStyle(String style) {
        BarStyle barStyle;
        try {
            barStyle = BarStyle.valueOf(style.toUpperCase());
        } catch (IllegalArgumentException e) {
            barStyle = BarStyle.SOLID;
            ExLog.sendWarning( "Estilo de la bossbar no es correcto: " + style);
        }
        return setStyle(barStyle);
    }

    public BossBar addFlag(BarFlag flag) {
        this.barFlags.add(flag);
        if (this.bossBar != null) {
            this.bossBar.addFlag(flag);
        }
        return this;
    }

    public BossBar removeFlag(BarFlag flag) {
        this.barFlags.remove(flag);
        if (bossBar != null) {
            this.bossBar.removeFlag(flag);
        }
        return this;
    }

    public BossBar clearFlags() {
        this.barFlags.clear();
        if (bossBar != null) {
            for (BarFlag flag : BarFlag.values()) {
                this.bossBar.removeFlag(flag);
            }
        }
        return this;
    }

    public BossBar removeAnDeath(boolean value) {
        this.deleteAnDeath = value;
        return this;
    }

    @FunctionalInterface
    public interface BossBarTick {
        void run(BossBar bossBar);
    }

    public BossBar onTick(BossBarTick tick) {
        this.onTick = tick;
        return this;
    }

    private void createBossBar() {
        if (bossBar != null) {
            this.bossBar.removeAll();
        }
        this.bossBar = Bukkit.createBossBar(title, color, style, barFlags.toArray(new BarFlag[0]));
        this.bossBar.setVisible(true);
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                this.bossBar.addPlayer(player);
            }
        }
    }

    public void remove() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar.setVisible(false);
        }

        players.clear();
        running = false;
        this.timeLeft = 0L;
        this.totalTime = 0L;
    }

    public void stop() {
        if (task != null) {
            this.task.cancel();
            this.task = null;
        }
        this.running = false;
        this.timeLeft = 0L;
        this.totalTime = 0L;

        if (this.bossBar != null) {
            this.clearPlayers();
            this.bossBar.removeAll();
            this.bossBar.setVisible(false);
        }
    }

    public void destroy() {
        this.stop();
        this.bossBar = null;
    }

    public void clearPlayers() {
        this.bossBar.removeAll();
        this.bossBar.setVisible(false);
        this.players.clear();
    }

    public boolean iaVisible() {
        return this.bossBar.isVisible();
    }

    public void start() {
        if (running) {
            ExLog.sendLogger(Logger.WARNING, "Ya hay una bossbar activa. No se puede iniciar otra.", true);
            return;
        }

        if (timeLeft <= 0) {
            ExLog.sendLogger(Logger.WARNING, "No se puede iniciar la bossbar sin haber asignado un tiempo.", true);
            return;
        }

        if (players.isEmpty()) {
            ExLog.sendLogger(Logger.WARNING, "No se puede iniciar la bossbar sin jugadores asignados.", true);
            return;
        }

        this.running = true;
        this.totalTime = timeLeft;
        this.initialTime = timeLeft; // 🔸 Guarda el tiempo inicial
        if (bossBar == null) {
            this.createBossBar();
        } else {
            this.bossBar.setVisible(true);
        }
        this.startTimer();
    }

    public void pause() {
        if (!running || task == null) {
            ExLog.sendLogger(Logger.INFO, "La bossbar no está activa. No se puede pausar.", true);
            return;
        }

        this.task.cancel();
        this.task = null;
        this.running = false;
        ExLog.sendLogger(Logger.INFO, "La bossbar ha sido pausada.", true);
    }

    public void resume() {
        if (running) {
            ExLog.sendLogger(Logger.WARNING, "La bossbar ya está activa. No se puede reanudar.", true);
            return;
        }

        if (timeLeft <= 0) {
            ExLog.sendLogger(Logger.WARNING, "No se puede reanudar la bossbar: no queda tiempo.", true);
            return;
        }

        if (players.isEmpty()) {
            ExLog.sendLogger(Logger.WARNING, "No se puede reanudar la bossbar sin jugadores asignados.", true);
            return;
        }

        this.running = true;
        this.startTimer();
        ExLog.sendLogger(Logger.INFO, "La bossbar ha sido reanudada.", true);
    }

    public void restart() {
        if (players.isEmpty()) {
            ExLog.sendLogger(Logger.WARNING, "No se puede reiniciar la bossbar sin jugadores asignados.", true);
            return;
        }

        if (initialTime <= 0) {
            ExLog.sendLogger(Logger.WARNING, "No se puede reiniciar la bossbar: no se ha definido un tiempo inicial.", true);
            return;
        }

        if (task != null) {
            this.task.cancel();
        }

        this.timeLeft = initialTime;
        this.totalTime = initialTime;
        this.running = true;
        this.startTimer();
        ExLog.sendLogger(Logger.INFO, "La bossbar ha sido reiniciada desde " + initialTime + " segundos.", true);
    }

    private void startTimer() {
        this.task = Scheduler.runTimer(() -> {
                if (timeLeft <= 0 || !hasOnlinePlayers()) {
                    if (onFinish != null) onFinish.run();
                    remove();
                    return;
                }

                var iterator = players.iterator();
                while (iterator.hasNext()) {
                    UUID uuid = iterator.next();
                    Player player = Bukkit.getPlayer(uuid);

                    if (player == null || !player.isOnline() || (deleteAnDeath && player.isDead())) {
                        if (player != null) {
                            bossBar.removePlayer(player);
                        }
                        iterator.remove();
                    }
                }

                if (onTick != null) {
                    try {
                        onTick.run(BossBar.this);
                    } catch (Exception e) {
                        ExLog.sendWarning("Error en onTick de la bossbar: " + e.getMessage());
                    }
                }

                if (bossBar != null) {
                    double progress = Math.max(0, Math.min(1, timeLeft / (double) totalTime));
                    bossBar.setProgress(progress);
                    bossBar.setTitle(utilsManagers.setColoredMessage(title.replace("%time%", utilsManagers.formatTime(timeLeft, true))));
                }

                timeLeft -= 1000;
        }, 0L, 20L);
    }

    private boolean hasOnlinePlayers() {
        return players.stream()
                .map(Bukkit::getPlayer)
                .anyMatch(player -> player != null && player.isOnline());
    }

    public long getTimeLeft() {
        return this.timeLeft;
    }

    public long getTotalTime() {
        return this.totalTime;
    }

    public org.bukkit.boss.BossBar getBossBar() {
        return this.bossBar;
    }

    public boolean isRunning() {
        return this.running;
    }

    public Task getTask() {
        return this.task;
    }

    public Set<UUID> getPlayers() {
        return this.players;
    }

    public List<Player> getPlayersOline() {
        List<Player> player = new ArrayList<>();
        for (var p : this.players) {
            if (Bukkit.getPlayer(p) != null) {
                player.add(Bukkit.getPlayer(p));
            }
        }
        return player;
    }
}