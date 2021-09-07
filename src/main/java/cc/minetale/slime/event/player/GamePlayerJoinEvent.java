package cc.minetale.slime.event.player;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import cc.minetale.slime.event.trait.GamePlayerEvent;
import lombok.Getter;
import net.minestom.server.event.trait.CancellableEvent;
import org.jetbrains.annotations.NotNull;

public class GamePlayerJoinEvent implements GamePlayerEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private Game game;
    @Getter private GamePlayer gamePlayer;

    public GamePlayerJoinEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer) {
        this.game = game;
        this.gamePlayer = gamePlayer;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
