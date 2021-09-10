package cc.minetale.slime.core;

public enum EndCondition implements IEndCondition {
    //Auto (The end condition either doesn't need to or can't be checked for manually)

    /** Last team/player to have more than 0 lives left. **/
    LAST_ALIVE,

    //Manual (The end condition needs to be checked for manually [or if the game's time limit ends])

    /** Player with the best score. **/
    PLAYER_BEST_SCORE,

    /** Team with the best score after <strong>adding</strong> all player scores **/
    TEAM_BEST_SCORE_ADD,
    /** Team with the best score after <strong>averaging</strong> all player scores **/
    TEAM_BEST_SCORE_AVG
}