package cc.minetale.slime.perceive;

import cc.minetale.slime.player.GamePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Able to have its {@linkplain PerceiveTeam} and visibility affected/changed. <br>
 * Named this way as you are able to manipulate ones perception, delude. <br>
 * <br>
 * Main implementation is {@linkplain GamePlayer} <br>
 * More info at {@linkplain PerceiveTeam}
 */
public interface IDelusory {

    // Perceive Teams

    Map<GamePlayer, SortedMap<Integer, PerceiveTeam>> getAllPerceivedAs();

    /**
     * Returns the {@linkplain PerceiveTeam} that the player is perceiving the target as. <br>
     * It is the team with the highest weight that the target can be perceived by the player as. <br>
     * <br>
     * See {@linkplain #targetPossiblyPerceivedAs(GamePlayer)} to get all possible teams the target can be perceived as.
     *
     * @return null if the target isn't being perceived as any {@linkplain PerceiveTeam} by the player
     */
    default @Nullable PerceiveTeam targetPerceivedAs(GamePlayer target) {
        var weight = targetPerceivedAsTeamWeight(target);
        if(weight < 0) { return null; }

        return targetPossiblyPerceivedAs(target).get(weight);
    }

    /**
     * Returns the weight of {@linkplain PerceiveTeam} that the player is perceiving the target as. <br>
     * It is the team with the highest weight that the target can be perceived by the player as. <br>
     * <br>
     * See {@linkplain #targetPossiblyPerceivedAs(GamePlayer)} to get all possible teams the target can be perceived as. <br>
     * Also see {@linkplain #targetPerceivedAs(GamePlayer)} to get the {@linkplain PerceiveTeam} that the target is being perceived by the player as.
     *
     * @return -1 if the target isn't being perceived as any {@linkplain PerceiveTeam} by the player
     */
    default int targetPerceivedAsTeamWeight(GamePlayer target) {
        SortedMap<Integer, PerceiveTeam> possibleTeams = targetPossiblyPerceivedAs(target);
        if(possibleTeams == null || possibleTeams.isEmpty()) { return -1; }

        return possibleTeams.lastKey();
    }

    /**
     * Returns the {@linkplain PerceiveTeam}(s) that the player can perceive the target as. <br>
     * <br>
     * See {@linkplain #targetPerceivedAs(GamePlayer)} to get the team that the player <strong>will</strong> perceive the target as.
     *
     * @return empty {@linkplain java.util.TreeMap} if target isn't being perceived as any {@linkplain PerceiveTeam} by the player
     */
    default @Nullable SortedMap<Integer, PerceiveTeam> targetPossiblyPerceivedAs(GamePlayer target) {
        return getAllPerceivedAs().get(target);
    }

    @Nullable PerceiveTeam perceiveTargetAs(PerceiveTeam perceiveAs, int weight, GamePlayer target);

    default void perceiveTargetsAs(PerceiveTeam perceiveAs, int weight, Collection<GamePlayer> targets) {
        for(var target : targets) {
            perceiveTargetAs(perceiveAs, weight, target);
        }
    }

    default void perceiveTargetsAs(PerceiveTeam perceiveAs, int weight, GamePlayer... targets) {
        perceiveTargetsAs(perceiveAs, weight, List.of(targets));
    }

    List<PerceiveTeam> removePerceivedTarget(GamePlayer target);

    default Set<PerceiveTeam> removePerceivedTargets(Collection<GamePlayer> targets) {
        Set<PerceiveTeam> removed = new HashSet<>();

        for(var target : targets) {
            removed.addAll(removePerceivedTarget(target));
        }

        return removed;
    }

    default void removePerceivedTargets(GamePlayer... targets) {
        removePerceivedTargets(List.of(targets));
    }

    boolean removePerceivedTargetTeam(GamePlayer target, PerceiveTeam team);

    default @Nullable PerceiveTeam removePerceivedTargetHighestTeam(GamePlayer target) {
        var perceivedAs = targetPerceivedAs(target);
        return removePerceivedTargetTeam(target, perceivedAs) ? perceivedAs : null;
    }

    List<GamePlayer> removeAllPerceivedTeam(PerceiveTeam team);

    default Set<GamePlayer> removeAllPerceivedTeams(Collection<PerceiveTeam> teams) {
        Set<GamePlayer> removed = new HashSet<>();

        for(var team : teams) {
            removed.addAll(removeAllPerceivedTeam(team));
        }

        return removed;
    }

    default void removeAllPerceivedTeams(PerceiveTeam... teams) {
        removeAllPerceivedTeams(List.of(teams));
    }

    default void removeAllPerceived() {
        Map<GamePlayer, SortedMap<Integer, PerceiveTeam>> perceivedAs = getAllPerceivedAs();
        for(var delusory : perceivedAs.keySet()) {
            removePerceivedTarget(delusory);
        }
    }

    // Hiding players on tab

    Set<GamePlayer> getHiddenTabPlayers();

    default boolean isTargetHiddenInTab(GamePlayer target) {
        return getHiddenTabPlayers().contains(target);
    }

    boolean hideTabTarget(GamePlayer target);
    boolean showTabTarget(GamePlayer target);

    // Refreshing targets

    void refreshTarget(GamePlayer target);

    default void refreshTargets(Collection<GamePlayer> targets) {
        for(var target : targets) {
            refreshTarget(target);
        }
    }

    default void refreshTargets(GamePlayer... targets) {
        refreshTargets(List.of(targets));
    }

}
