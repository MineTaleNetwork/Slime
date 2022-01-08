package cc.minetale.slime.core;

import cc.minetale.slime.event.game.PostGameStageChangeEvent;
import cc.minetale.slime.event.game.PreGameStageChangeEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.IStage;
import cc.minetale.slime.game.Stage;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.EventDispatcher;

public class GameState {

    @Getter @Setter private Game game;

    @Getter protected IStage stage = Stage.IN_LOBBY;

    public void setStage(IStage stage) {
        var preEvent = new PreGameStageChangeEvent(game, this.stage, stage);
        EventDispatcher.call(preEvent);

        if(!preEvent.isCancelled()) {
            this.stage = preEvent.getNewStage();

            var postEvent = new PostGameStageChangeEvent(game, this.stage, stage);
            EventDispatcher.call(postEvent);
        }
    }

    public boolean inLobby() {
        return this.stage == Stage.IN_LOBBY || this.stage == Stage.STARTING;
    }

}
