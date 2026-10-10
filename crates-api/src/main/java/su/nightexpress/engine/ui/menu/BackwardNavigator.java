package su.nightexpress.engine.ui.menu;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.engine.scheduler.PluginScheduler;

@NullMarked
public interface BackwardNavigator {

    BackwardNavigator CLOSE_INVENTORY = player -> PluginScheduler.runOnEntity(player, player::closeInventory);

    void moveBack(Player player);

    static void run(@Nullable BackwardNavigator navigator, Player player) {
        if (navigator == null) return;
        PluginScheduler.runOnEntity(player, () -> navigator.moveBack(player));
    }
}
