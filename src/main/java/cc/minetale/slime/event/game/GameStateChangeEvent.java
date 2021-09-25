package cc.minetale.slime.event.game;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.state.IBaseState;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;

public class GameStateChangeEvent implements GameEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private final Game game;
    @Getter private final IBaseState previousState;
    @Getter @Setter private IBaseState newState;

    public GameStateChangeEvent(Game game, IBaseState previousState, IBaseState newState) {
        this.game = game;
        this.previousState = previousState;
        this.newState = newState;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
