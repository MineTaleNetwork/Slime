package cc.minetale.slime.event.game;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.state.IStage;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;

public class GameStageChangeEvent implements GameEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private final Game game;
    @Getter private final IStage previousStage;
    @Getter @Setter private IStage newStage;

    public GameStageChangeEvent(Game game, IStage previousStage, IStage newStage) {
        this.game = game;
        this.previousStage = previousStage;
        this.newStage = newStage;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
