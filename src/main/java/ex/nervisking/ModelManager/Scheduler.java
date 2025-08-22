package ex.nervisking.ModelManager;

import ex.nervisking.ExApi;
import ex.nervisking.utils.methods.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Clase utilitaria para ejecutar tareas en el scheduler de Bukkit de forma simplificada.
 */
public final class Scheduler {

    /**
     * Ejecuta una tarea sin demora en el hilo principal.
     *
     * @param runnable la tarea a ejecutar
     */
    public static void run(Runnable runnable) {
        Bukkit.getScheduler().runTask(ExApi.getPlugin(), runnable);
    }

    /**
     * Ejecuta una tarea sin demora en un hilo asíncrono.
     *
     * @param runnable la tarea a ejecutar
     */
    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(ExApi.getPlugin(), runnable);
    }

    /**
     * Ejecuta una tarea una sola vez después de un retraso especificado (en ticks) en el hilo principal.
     *
     * @param runnable    la tarea a ejecutar
     * @param delayTicks  el retraso en ticks antes de ejecutar
     * @return una instancia de {@link Task} para controlar la tarea
     */
    @Contract("_, _ -> new")
    public static @NotNull Task runLater(Runnable runnable, long delayTicks) {
        return new Task(Bukkit.getScheduler().runTaskLater(ExApi.getPlugin(), runnable, delayTicks));
    }

    public static void runTimer(Consumer<Task> action, long delayTicks, long periodTicks) {
        final Task[] holder = new Task[1];

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(ExApi.getPlugin(), () -> {
            action.accept(holder[0]); // Aquí el código de la tarea recibe su Task
        }, delayTicks, periodTicks);

        Task task = new Task(bukkitTask);
        holder[0] = task;

    }

    /**
     * Ejecuta una tarea una sola vez después de un retraso especificado (en ticks) en un hilo asíncrono.
     *
     * @param runnable    la tarea a ejecutar
     * @param delayTicks  el retraso en ticks antes de ejecutar
     * @return una instancia de {@link Task} para controlar la tarea
     */
    @Contract("_, _ -> new")
    public static @NotNull Task runLaterAsync(Runnable runnable, long delayTicks) {
        return new Task(Bukkit.getScheduler().runTaskLaterAsynchronously(ExApi.getPlugin(), runnable, delayTicks));
    }

    /**
     * Ejecuta una tarea repetitiva en el hilo principal.
     *
     * @param runnable     la tarea a ejecutar
     * @param delayTicks   el retraso inicial en ticks antes de la primera ejecución
     * @param periodTicks  el período en ticks entre ejecuciones
     * @return una instancia de {@link Task} para controlar la tarea
     */
    @Contract("_, _, _ -> new")
    public static @NotNull Task runTimer(Runnable runnable, long delayTicks, long periodTicks) {
        return new Task(Bukkit.getScheduler().runTaskTimer(ExApi.getPlugin(), runnable, delayTicks, periodTicks));
    }

    /**
     * Ejecuta una tarea repetitiva de forma asíncrona.
     *
     * @param runnable     la tarea a ejecutar
     * @param delayTicks   el retraso inicial en ticks antes de la primera ejecución
     * @param periodTicks  el período en ticks entre ejecuciones
     * @return una instancia de {@link Task} para controlar la tarea
     */
    @Contract("_, _, _ -> new")
    public static @NotNull Task runTimerAsync(Runnable runnable, long delayTicks, long periodTicks) {
        return new Task(Bukkit.getScheduler().runTaskTimerAsynchronously(ExApi.getPlugin(), runnable, delayTicks, periodTicks));
    }


    /**
     * Ejecuta una tarea inmediatamente si ya está en el hilo principal; si no, la agenda sin demora.
     */
    public static void runSyncIfNeeded(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            run(runnable);
        }
    }

    /**
     * Ejecuta una tarea después de un delay definido por string con formato personalizado.
     * Ejemplos: "1d10m", "5m;35s;4t", "1h 30m 20s", "500ms"
     */
    public static @NotNull Task runLater(Runnable runnable, String delayString) {
        long ticks = TimeParser.parseToTicks(delayString);
        return runLater(runnable, ticks);
    }


    /**
     * Cancela todas las tareas activas del plugin.
     */
    public static void cancelAllPluginTasks() {
        Bukkit.getScheduler().cancelTasks(ExApi.getPlugin());
    }

    /**
     * Convierte una {@link Duration} a ticks (1 tick = 50ms).
     */
    @Contract(pure = true)
    private static long durationToTicks(@NotNull Duration duration) {
        return duration.toMillis() / 50;
    }

    /**
     * Ejecuta una tarea en el hilo principal y devuelve el resultado de forma asincrónica.
     */
    public static <T> @NotNull CompletableFuture<T> callSync(Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        run(() -> {
            try {
                future.complete(supplier.get());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /**
     * Ejecuta una tarea asincrónica que devuelve un valor en el futuro.
     */
    @Contract("_ -> new")
    public static <T> @NotNull CompletableFuture<T> callAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier);
    }

    /**
     * Ejecuta una tarea en segundo plano y luego pasa el resultado al hilo principal.
     */
    public static <T> void runAsyncThenSync(Supplier<T> asyncSupplier, Consumer<T> syncConsumer) {
        callAsync(asyncSupplier).thenAccept(result -> run(() -> syncConsumer.accept(result)));
    }
}