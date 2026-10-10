package su.nightexpress.engine.scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;

@NullMarked
public final class PluginScheduler {

    private static volatile @Nullable TaskScheduler scheduler;

    private PluginScheduler() {}

    public static void bind(@Nullable TaskScheduler taskScheduler) {
        scheduler = taskScheduler;
    }

    public static TaskScheduler get() {
        TaskScheduler current = scheduler;
        if (current == null) {
            throw new IllegalStateException("Task scheduler is not initialized");
        }
        return current;
    }

    public static long secondsToTicks(int seconds) {
        return seconds * 20L;
    }

    public static void runGlobal(Runnable task) {
        TaskScheduler current = get();
        if (current.isGlobalThread()) {
            task.run();
            return;
        }
        current.runTask(task);
    }

    public static void runOnEntity(Entity entity, Runnable task) {
        TaskScheduler current = get();
        if (current.isEntityThread(entity)) {
            task.run();
            return;
        }
        current.runTask(entity, task);
    }

    public static <T> CompletableFuture<T> callOnEntity(Entity entity, Supplier<T> supplier) {
        TaskScheduler current = get();
        if (current.isEntityThread(entity)) {
            return CompletableFuture.completedFuture(supplier.get());
        }

        CompletableFuture<T> future = new CompletableFuture<>();
        current.runTask(entity, () -> {
            try {
                future.complete(supplier.get());
            }
            catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });
        return future;
    }

    public static void runOnRegion(Location location, Runnable task) {
        if (location.getWorld() == null) return;

        TaskScheduler current = get();
        if (current.isRegionThread(location)) {
            task.run();
            return;
        }
        current.runTask(location, task);
    }
}
