package cc.minetale.slime.rule;

import cc.minetale.slime.team.GameTeam;
import cc.minetale.slime.utils.restriction.Blacklist;
import cc.minetale.slime.utils.restriction.RestrictionList;
import net.minestom.server.item.Material;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TeamRule<T> extends Rule<T> {

    public static final Set<TeamRule<?>> ALL_RULES = Collections.synchronizedSet(new HashSet<>());

    public TeamRule(T defaultValue) {
        super(defaultValue);
        ALL_RULES.add(this);
    }

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * You can attack your teammates.
     */
    public static final TeamRule<Boolean> ATTACK_ALLIES = new TeamRule<>(false);

    /**
     * {@linkplain RestrictionList}&lt;{@linkplain Material}&gt; <br>
     * <br>
     * You can only attack players from teams that aren't restricted ({@linkplain RestrictionList#isRestricted(Object)}).
     */
    public static final TeamRule<RestrictionList<GameTeam>> ATTACK_TEAMS = new TeamRule<>(Blacklist.empty());

}
