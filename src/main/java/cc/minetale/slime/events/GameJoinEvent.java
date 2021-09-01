package cc.minetale.slime.events;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class GameJoinEvent<G extends Game<G,P,?>, P extends GamePlayer<P,?,G>> implements PlayerEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private G game;
    @Getter private P gamePlayer;

    public GameJoinEvent(@NotNull G game, @NotNull P gamePlayer) {
        this.game = game;
        this.gamePlayer = gamePlayer;
    }

    @Override public @NotNull Player getPlayer() {
        return this.gamePlayer.getHandle();
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
