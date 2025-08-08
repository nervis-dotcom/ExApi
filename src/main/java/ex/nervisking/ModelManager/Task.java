package ex.nervisking.ModelManager;

import org.bukkit.scheduler.BukkitTask;

public class Task {

    private final BukkitTask bukkitTask;

    Task(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    public void cancel() {
        if (bukkitTask != null && !bukkitTask.isCancelled()) {
            bukkitTask.cancel();
        }
    }

    public boolean isRunning() {
        return bukkitTask != null && !bukkitTask.isCancelled();
    }
}