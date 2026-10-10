package su.nightexpress.engine.component;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;

import su.nightexpress.engine.scheduler.PluginScheduler;
import su.nightexpress.nightcore.NightCorePlugin;

@NullMarked
public abstract class BaseController extends BasePluginComponent implements Listener {

    protected final NightCorePlugin    plugin;
    protected final List<MyScheduledTask> tasks;

    public BaseController(NightCorePlugin plugin) {
        super();
        this.plugin = plugin;
        this.tasks = new ArrayList<>();
    }

    protected abstract void onControllerStart();

    protected abstract void onControllerShutdown();

    protected abstract void onControllerReload();

    @Override
    protected final void onStart() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

        this.onControllerStart();
    }

    @Override
    protected final void onShutdown() {
        // Automatically kill all tasks started by this controller
        this.stopTasks();

        // Automatically unregister all Bukkit events inside this class
        HandlerList.unregisterAll(this);

        // Fire an optional cleanup method for subclasses
        this.onControllerShutdown();
    }

    @Override
    protected final void onReload() {
        this.onControllerReload();
    }

    public void stopTasks() {
        this.tasks.forEach(MyScheduledTask::cancel);
        this.tasks.clear();
    }

    protected void addSecondsTask(Runnable runnable, int interval) {
        this.addRepeatingTask(runnable, PluginScheduler.secondsToTicks(interval), false);
    }

    protected void addTickTask(Runnable runnable, long interval) {
        this.addRepeatingTask(runnable, interval, false);
    }

    protected void addAsyncSecondsTask(Runnable runnable, int interval) {
        this.addRepeatingTask(runnable, PluginScheduler.secondsToTicks(interval), true);
    }

    protected void addAsyncTickTask(Runnable runnable, long interval) {
        this.addRepeatingTask(runnable, interval, true);
    }

    private void addRepeatingTask(Runnable runnable, long intervalTicks, boolean async) {
        if (intervalTicks <= 0L) return;

        TaskScheduler scheduler = PluginScheduler.get();
        // Folia rejects a non-positive initial delay. One tick still starts on the next cycle.
        long delay = 1L;
        MyScheduledTask task = async
            ? scheduler.runTaskTimerAsynchronously(runnable, delay, intervalTicks)
            : scheduler.runTaskTimer(runnable, delay, intervalTicks);
        this.tasks.add(task);
    }
}