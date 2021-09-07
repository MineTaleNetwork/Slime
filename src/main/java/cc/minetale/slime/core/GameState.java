package cc.minetale.slime.core;

import cc.minetale.slime.event.GameStateChangeEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.EventDispatcher;

public class GameState {

    @Getter @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Getter protected State baseState = State.IN_LOBBY;

    public void setBaseState(State state) {
        var event = new GameStateChangeEvent(game, this.baseState, state);
        EventDispatcher.call(event);

        this.baseState = event.getNewState();
    }

    public boolean isJoinable() {
        return this.baseState == State.IN_LOBBY || this.baseState == State.STARTING;
    }

    public enum State {
        IN_LOBBY,   //Gathering players in a lobby
        STARTING,   //Enough players, starting the game

        PRE_GAME,   //The game is preparing like explaining the gamemode and counting down
        GAME,       //The game is ongoing, this includes the grace period
        POST_GAME   //The game has ended, display the scoreboard, play win effects
    }

}
