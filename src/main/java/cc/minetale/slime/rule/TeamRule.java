package cc.minetale.slime.rule;

import cc.minetale.slime.team.GameTeam;
import cc.minetale.slime.misc.restriction.Blacklist;
import cc.minetale.slime.misc.restriction.RestrictionList;
import net.minestom.server.item.Material;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class TeamRule<T> extends Rule<T> {

    public static final Set<TeamRule<?>> ALL_TEAM_RULES = Collections.synchronizedSet(new HashSet<>());

    public static final IRuleType TYPE = new RuleType(true, GameTeam.class);

    public TeamRule(T defaultValue) {
        super(defaultValue);
        ALL_TEAM_RULES.add(this);
    }

    public TeamRule(T defaultValue, BiConsumer<GameTeam, T> onRuleChange) {
        super(defaultValue, (BiConsumer) onRuleChange);
        ALL_TEAM_RULES.add(this);
    }

    @Override
    public IRuleType getType() {
        return TYPE;
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
