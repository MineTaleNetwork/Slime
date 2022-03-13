package cc.minetale.slime.event.game;

import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.Stage;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;

/**
 * Use this event only if you want to change the stage the game will be turned to. <br>
 * Otherwise use {@linkplain PostGameStageChangeEvent}.
 */
public class PreGameStageChangeEvent implements GameEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private final Game game;
    @Getter private final Stage previousStage;
    @Getter @Setter private Stage newStage;

    public PreGameStageChangeEvent(Game game, Stage previousStage, Stage newStage) {
        this.game = game;
        this.previousStage = previousStage;
        this.newStage = newStage;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
