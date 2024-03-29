package cc.minetale.slime.event.game;

import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.game.Game;
import lombok.Getter;
import net.minestom.server.event.trait.CancellableEvent;

public class GameCreateEvent implements GameEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private final Game game;

    public GameCreateEvent(Game game) {
        this.game = game;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
