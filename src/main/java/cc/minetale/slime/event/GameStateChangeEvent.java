package cc.minetale.slime.event;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GameState.State;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;

public class GameStateChangeEvent implements CancellableEvent {

    private boolean cancelled;

    @Getter private final Game game;
    @Getter private final State previousState;
    @Getter @Setter private State newState;

    public GameStateChangeEvent(Game game, State previousState, State newState) {
        this.game = game;
        this.previousState = previousState;
        this.newState = newState;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
