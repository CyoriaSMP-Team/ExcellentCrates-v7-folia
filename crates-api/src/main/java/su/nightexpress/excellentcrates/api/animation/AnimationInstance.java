package su.nightexpress.excellentcrates.api.animation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AnimationInstance {

    boolean isRunning();

    void start();

    boolean tick();

    void stop();

    Player player();

    Location location();
}
