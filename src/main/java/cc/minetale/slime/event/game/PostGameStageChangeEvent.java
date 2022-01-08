package cc.minetale.slime.event.game;

import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.IStage;
import lombok.Getter;

/**
 * Use this event to do advanced logic after the stage has been changed. <br>
 * Prefer this event over {@linkplain PreGameStageChangeEvent} as here the game's stage has been already changed, and acknowledged.
 * Usage of the aforementioned event may cause unexpected game's logic and behaviour.
 */
public class PostGameStageChangeEvent implements GameEvent {

    @Getter private final Game game;
    @Getter private final IStage previousStage;
    @Getter private final IStage newStage;

    public PostGameStageChangeEvent(Game game, IStage previousStage, IStage newStage) {
        this.game = game;
        this.previousStage = previousStage;
        this.newStage = newStage;
    }

}
