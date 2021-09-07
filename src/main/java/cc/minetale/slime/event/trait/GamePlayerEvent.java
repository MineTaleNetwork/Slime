package cc.minetale.slime.event.trait;

import cc.minetale.slime.core.GamePlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public interface GamePlayerEvent extends PlayerEvent {
    GamePlayer getGamePlayer();

    @Override
    default @NotNull Player getPlayer() {
        return getGamePlayer().getHandle();
    }
}
