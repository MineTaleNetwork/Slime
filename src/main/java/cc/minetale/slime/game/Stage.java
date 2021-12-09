package cc.minetale.slime.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The default implementation for the {@linkplain IStage}. <br>
 * If you wish to have additional states, you are free to do so, but you mustn't replace and still use these.
 */
@AllArgsConstructor
public enum Stage implements IStage {
    /** Setting up the arena like creating teams and spawns. **/
    SETUP(0),
    /** Gathering players in a lobby. **/
    IN_LOBBY(1),
    /** Enough players, starting the game. **/
    STARTING(2),

    /** The game is preparing like explaining the gamemode and counting down. **/
    PRE_GAME(3),
    /**
     * The game is ongoing, this includes the grace period or anything similar.<br>
     * Any additional stages should be between this one and {@linkplain #POST_GAME}.
     */
    IN_GAME(4),
    /** The game has ended, display the scoreboard, play win effects. **/
    POST_GAME(Integer.MAX_VALUE);

    @Getter private int order;
}