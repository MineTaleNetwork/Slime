package cc.minetale.slime.core;

import cc.minetale.slime.perceive.PerceiveTeam;

/**
 * Determines if the players in teams should be grouped by specified teams (e.g. red, blue, zombies) <br>
 * or not (e.g. every opposite team appears a different color). <br>
 * <br>
 * {@linkplain #ANONYMOUS} should be generally used for FFA games or ones where there is a lot of teams possible <br>
 * that are otherwise impossible to distinguish in a sensible way (e.g. battle royale).
 */
public enum TeamStyle {
    SPECIFIED,
    /** Backed by {@linkplain PerceiveTeam} */
    ANONYMOUS
}
