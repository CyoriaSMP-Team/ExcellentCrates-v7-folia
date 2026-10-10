package su.nightexpress.excellentcrates.animation.session;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

import su.nightexpress.engine.scheduler.PluginScheduler;
import su.nightexpress.engine.component.BaseController;
import su.nightexpress.excellentcrates.api.CratesPlugin;
import su.nightexpress.excellentcrates.api.animation.AnimationInstance;

@NullMarked
public class AnimationSessionController extends BaseController {

    private final AnimationSessionManager sessionManager;

    public AnimationSessionController(CratesPlugin plugin, AnimationSessionManager sessionManager) {
        super(plugin);
        this.sessionManager = sessionManager;
    }

    @Override
    protected void onControllerReload() {

    }

    @Override
    protected void onControllerShutdown() {
        this.stopAllSessions();
    }

    @Override
    protected void onControllerStart() {
        this.runInstanceTickTask();
    }

    private void runInstanceTickTask() {
        this.addTickTask(this::tickSessions, 1);
    }

    private void tickSessions() {
        this.sessionManager.purgeStoppedSessions();

        for (AnimationInstance session : this.sessionManager.getSessions()) {
            this.runOnAnimationRegion(session, () -> {
                if (!session.isRunning()) return;
                if (!session.tick()) {
                    session.stop();
                }
            });
        }
    }

    private void stopAllSessions() {
        for (AnimationInstance session : this.sessionManager.getSessions()) {
            this.runOnAnimationRegion(session, session::stop);
        }
    }

    private void stopSession(Player player) {
        AnimationInstance session = this.sessionManager.getSession(player.getUniqueId());
        if (session == null) return;

        UUID playerId = player.getUniqueId();
        this.runOnAnimationRegion(session, () -> this.sessionManager.stopSession(playerId));
    }

    private void runOnAnimationRegion(AnimationInstance session, Runnable task) {
        Location location = session.location();
        if (location.getWorld() == null) {
            task.run();
            return;
        }
        PluginScheduler.runOnRegion(location, task);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.stopSession(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        this.stopSession(event.getPlayer());
    }
}
