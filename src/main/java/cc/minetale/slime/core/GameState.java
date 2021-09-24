package cc.minetale.slime.core;

import cc.minetale.slime.event.game.GameStateChangeEvent;
import cc.minetale.slime.state.BaseState;
import cc.minetale.slime.state.IBaseState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.EventDispatcher;

public class GameState {

    @Getter @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Getter protected IBaseState baseState = BaseState.IN_LOBBY;

    public void setBaseState(IBaseState state) {
        var event = new GameStateChangeEvent(game, this.baseState, state);
        EventDispatcher.call(event);

        this.baseState = event.getNewState();
    }

    public boolean inLobby() {
        return this.baseState == BaseState.IN_LOBBY || this.baseState == BaseState.STARTING;
    }

}
