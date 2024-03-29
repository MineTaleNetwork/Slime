package cc.minetale.slime.utils;

import lombok.experimental.UtilityClass;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

import static cc.minetale.slime.Slime.INSTANCE_MANAGER;

@UtilityClass
public final class InstanceUtil {
    public static void kickAll(Instance instance) {
        instance.getPlayers().forEach(Player::remove);
    }

    public static void unregisterSafe(Instance instance) {
        kickAll(instance);
        INSTANCE_MANAGER.unregisterInstance(instance);
    }
}
