package cc.minetale.slime.event.player;

import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.event.trait.GamePlayerEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.player.IPlayerState;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GamePlayerStateChangeEvent implements GameEvent, GamePlayerEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private Game game;
    @Getter private GamePlayer gamePlayer;

    @Getter @Nullable private IPlayerState previousState;
    @Getter @Setter @NotNull private IPlayerState newState;

    public GamePlayerStateChangeEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer, IPlayerState previousState, IPlayerState newState) {
        this.game = game;
        this.gamePlayer = gamePlayer;

        this.previousState = previousState;
        this.newState = newState;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
