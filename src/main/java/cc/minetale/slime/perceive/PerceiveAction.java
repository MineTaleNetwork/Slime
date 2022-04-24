package cc.minetale.slime.perceive;

import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.player.PlayerState;
import cc.minetale.slime.core.TargetAudience;
import org.jetbrains.annotations.Nullable;

public record PerceiveAction(Type type, TargetAudience target, @Nullable PerceiveTeam team, int weight) {
    /** For {@linkplain Type#START} and {@linkplain Type#STOP} */
    public PerceiveAction(Type type, TargetAudience target, @Nullable PerceiveTeam team) {
        this(type, target, team, 0);
    }

    /** For {@linkplain Type#START} and {@linkplain Type#STOP} */
    public PerceiveAction(Type type, @Nullable PerceiveTeam team, int weight) {
        this(type, TargetAudience.GAME, team, weight);
    }

    /** For {@linkplain Type#START} and {@linkplain Type#STOP} */
    public PerceiveAction(Type type, @Nullable PerceiveTeam team) {
        this(type, TargetAudience.GAME, team, 0);
    }

    /** For {@linkplain Type#STOP_ALL} */
    public PerceiveAction(TargetAudience target) {
        this(Type.STOP_ALL, target, null);
    }

    /** For {@linkplain Type#STOP_ALL} */
    public PerceiveAction() {
        this(TargetAudience.GAME);
    }

    /** Will the {@linkplain #target} either start perceiving the player as {@linkplain #team} or stop (if it already did in the first place). */
    public enum Type {
        START,
        /** {@linkplain #weight} is ignored */
        STOP,
        /** {@linkplain #team} and {@linkplain #weight} are ignored */
        STOP_ALL
    }

    /**
     * Who will perceive/stop perceiving the player as {@linkplain #team}. <br>
     * Anything other than {@linkplain #SELF} will also include the player. <br>
     * <br>
     * This is <strong>only</strong> checked for in {@linkplain GamePlayer#setState(PlayerState)}
     */
    public enum Target {
        SELF,
        TEAM,
        GAME
    }
}
