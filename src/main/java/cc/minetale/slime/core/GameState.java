package cc.minetale.slime.core;

import cc.minetale.slime.event.game.PostGameStageChangeEvent;
import cc.minetale.slime.event.game.PreGameStageChangeEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.Stage;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.EventDispatcher;

public class GameState {

    @Getter @Setter private Game game;

    @Getter protected Stage stage = Stage.IN_LOBBY;

    public void setStage(Stage stage) {
        var preEvent = new PreGameStageChangeEvent(game, this.stage, stage);
        EventDispatcher.call(preEvent);

        if(!preEvent.isCancelled()) {
            this.stage = preEvent.getNewStage();

            var postEvent = new PostGameStageChangeEvent(game, this.stage, stage);
            EventDispatcher.call(postEvent);
        }
    }

    public void nextStage() {
        setStage(this.stage.getNext());
    }

    public void previousStage() {
        setStage(this.stage.getPrevious());
    }

    public boolean inLobby() {
        return this.stage == Stage.IN_LOBBY || this.stage == Stage.STARTING;
    }

}
