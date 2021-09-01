package cc.minetale.slime.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@FunctionalInterface
@RequiredArgsConstructor()
public class TeamAssigner
//        <G extends Game<G,P,?>, P extends GamePlayer<P,?,G>>
{

    //Game, Available Teams (in order of usage), Players -> Players assigned to teams
    protected final TriFunction<
            Game<?,?,?>,
            List<GameTeam<?,?,?>>,
            List<GamePlayer<?,?,?>>,
            Map<GamePlayer<?,?,?>, GameTeam<?,?,?>>> assigner;

    public static TeamAssigner simple(int teamSize) {
        return new TeamAssigner((game, teams, players) -> {
           for(int i = 0; i < teams.size(); i++) {
               GameTeam<?,?,?> team = teams.get(i);

           }
        });
    }

    public static final TeamAssigner ONES_ASSIGNER = new TeamAssigner((game, teams, players) -> {

    });

    public static final TeamAssigner TWOS_ASSIGNER = new TeamAssigner((game, teams, players) -> {

    });

    public static final TeamAssigner THREES_ASSIGNER = new TeamAssigner((game, teams, players) -> {

    });

    public static final TeamAssigner FOURS_ASSIGNER = new TeamAssigner((game, teams, players) -> {

    });

}
