package cc.minetale.slime.event;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GameState.State;
import cc.minetale.slime.event.trait.GameEvent;
import lombok.Getter;
import net.minestom.server.event.trait.CancellableEvent;

public class GameCreateEvent implements GameEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private final Game game;

    public GameCreateEvent(Game game, State previousState, State newState) {
        this.game = game;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
