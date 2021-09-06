package cc.minetale.slime.team;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;

import java.util.*;
import java.util.function.Supplier;

public abstract class TeamAssigner {

    /**
     * Assigns players to each team.
     * @param game Game the players are being assigned for
     * @param teamSupplier Team supplier
     * @param players Players to assign
     * @param <T> {@linkplain GameTeam} implementation to use
     * @param <P> {@linkplain GamePlayer} implementation to use
     * @return Players assigned to their teams. <br>
     * Can be used to do intermediate operations on players based on their teams before <br>
     * actually assigning them, usually through {@linkplain TeamAssigner#assignTeams(Map)}.
     */
    public abstract <T extends GameTeam, P extends GamePlayer> Map<T, Set<P>> assign(Game game,
                                                                                     Supplier<T> teamSupplier,
                                                                                     List<P> players);

    public static TeamAssigner simple(int teamSize) {
        return new TeamAssigner() {
            @Override public <T extends GameTeam, P extends GamePlayer> Map<T, Set<P>> assign(Game game,
                                                                                              Supplier<T> teamSupplier,
                                                                                              List<P> players) {

                var teamsAmount = (int) Math.ceil((double) players.size() / teamSize);
                Map<T, Set<P>> assignedTeams = new HashMap<>();

                List<T> availableTeams = ColorTeam.getRequiredTeams(teamsAmount, teamSupplier);
                for(T team : availableTeams) {
                    team.setSize(teamSize);

                    Set<P> teamPlayers = new HashSet<>();
                    for(int i = 0; i < teamSize; i++) {
                        var player = players.remove(0);
                        teamPlayers.add(player);
                    }

                    assignedTeams.put(team, teamPlayers);
                }

                return assignedTeams;
            }
        };
    }

    //TODO More complex TeamAssigner types (ELO, Parties)

    public static void assignTeams(Map<GameTeam, Set<GamePlayer>> teams) {
        teams.forEach(GameTeam::addPlayers);
    }

    public static final TeamAssigner[] SIMPLE = new TeamAssigner[10];
    static {
        for(int i = 0; i < SIMPLE.length; i++) {
            SIMPLE[i] = simple(i);
        }
    }

}
