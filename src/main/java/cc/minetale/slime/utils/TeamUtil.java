package cc.minetale.slime.utils;

import cc.minetale.commonlib.util.CollectionsUtil;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.team.GameTeam;
import cc.minetale.slime.team.ITeamType;
import cc.minetale.slime.team.TeamProvider;

import java.util.*;

public final class TeamUtil {

    private TeamUtil() {}

    public static <T extends ITeamType> T findById(Collection<T> types, String id) {
        for(T type : types) {
            if(type.getId().equals(id)) { return type; }
        }
        return null;
    }

    public static void assignPlayers(Map<GameTeam, Set<GamePlayer>> teams) {
        teams.forEach(GameTeam::addPlayers);
    }

    /**
     * Creates {@linkplain GameTeam} based on {@linkplain ITeamType}s without any restrictions. <br>
     * Also see {@linkplain #createTeams(TeamProvider, List, Game, int, int)}.
     * @param types Team types to create {@linkplain GameTeam}s from.
     * @param game Game to create all teams for.
     * @param teamSize Maximum amount of players for each team created (can be changed for any team after creating them).
     */
    public static List<GameTeam> createTeams(TeamProvider provider, List<ITeamType> types, Game game, int teamSize) {
        List<GameTeam> teams = new ArrayList<>();
        for(ITeamType type : types) {
            var team = provider.createTeam(game, type.getId(), teamSize, type);
            team.setType(type);
            team.setSize(teamSize);
            teams.add(team);
        }
        return teams;
    }

    /**
     * Creates {@linkplain GameTeam} based on {@linkplain ITeamType}s. <br>
     * Unlike {@linkplain #createTeams(TeamProvider, List, Game, int)} it creates enough teams to fill all players and then stops. <br>
     * This is the preferred way to create teams.
     * @param types Team types to create {@linkplain GameTeam}s from.
     * @param game Game to create all teams for.
     * @param teamSize Maximum amount of players for each team created (can be changed for any team after creating them).
     * @param players Amount of players that these teams are created for. <br>
     *                Stops creating teams after all currently created teams can accommodate all players.
     */
    public static List<GameTeam> createTeams(TeamProvider provider, List<ITeamType> types, Game game, int teamSize, int players) {
        List<GameTeam> teams = new ArrayList<>();
        //How many teams will be created
        var expectedTotal = Math.ceil((double) players / teamSize);
        var i = 0;
        for(ITeamType type : types) {
            var team = provider.createTeam(game, type.getId(), teamSize, type);
            team.setType(type);
            team.setSize(teamSize);
            teams.add(team);
            if(++i >= expectedTotal) { break; }
        }
        return teams;
    }

    /**
     * Creates {@linkplain GameTeam} based on {@linkplain ITeamType}s. <br>
     * Just like {@linkplain #createTeams(TeamProvider, List, Game, int, int)} it creates enough teams to fill all players and then stops, <br>
     * but also allows you to specify different sizes for each team. <br>
     * <br>
     * Keep in mind this method doesn't fill teams from the smallest size to biggest, <br>
     * but rather in order of {@linkplain Map#entrySet()}. <br>
     * This is why it's recommended to use a {@linkplain SortedMap} when using this method. <br>
     * <br>
     * This is the preferred way to create teams if the teams can vary in size.
     * @param types Team types to create {@linkplain GameTeam}s from including their maximum size.
     * @param game Game to create all teams for.
     * @param players Amount of players that these teams are created for. <br>
     *                Stops creating teams after all currently created teams can accommodate all players.
     */
    public static List<GameTeam> createTeams(TeamProvider provider, Map<ITeamType, Integer> types, Game game, int players) {
        List<GameTeam> teams = new ArrayList<>();
        var totalCapacity = CollectionsUtil.addAllInt(types.values());
        //How many teams will be created
        var expectedTotal = Math.ceil((double) players / totalCapacity);
        var i = 0;
        for(Map.Entry<ITeamType, Integer> ent : types.entrySet()) {
            var type = ent.getKey();
            var teamSize = ent.getValue();

            var team = provider.createTeam(game, type.getId(), teamSize, type);
            team.setType(type);
            team.setSize(teamSize);
            teams.add(team);
            if(++i >= expectedTotal) { break; }
        }
        return teams;
    }

}
