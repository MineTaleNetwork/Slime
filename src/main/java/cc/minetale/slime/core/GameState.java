package cc.minetale.slime.core;

import cc.minetale.slime.event.game.GameStageChangeEvent;
import cc.minetale.slime.state.IStage;
import cc.minetale.slime.state.Stage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.EventDispatcher;

public class GameState {

    @Getter @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Getter protected IStage stage = Stage.IN_LOBBY;

    public void setStage(IStage state) {
        var event = new GameStageChangeEvent(game, this.stage, state);
        EventDispatcher.call(event);

        this.stage = event.getNewStage();
    }

    public boolean inLobby() {
        return this.stage == Stage.IN_LOBBY || this.stage == Stage.STARTING;
    }

}
