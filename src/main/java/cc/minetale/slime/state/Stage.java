package cc.minetale.slime.state;

/**
 * The default implementation for the {@linkplain IStage}. <br>
 * If you wish to have additional states, you are free to do so, but you mustn't replace and still use these. <br>
 */
public enum Stage implements IStage {
    /** Gathering players in a lobby. **/
    IN_LOBBY,
    /** Enough players, starting the game. **/
    STARTING,

    /** The game is preparing like explaining the gamemode and counting down. **/
    PRE_GAME,
    /** The game is ongoing, this includes the grace period or anything similar. **/
    IN_GAME,
    /** The game has ended, display the scoreboard, play win effects. **/
    POST_GAME
}