package cc.minetale.slime.team;

import cc.minetale.slime.Slime;
import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;

import java.util.*;

public abstract class TeamAssigner {

    /**
     * Assigns players to each team.
     * @param game Game the players are being assigned for
     * @param players Players to assign
     * @param <P> {@linkplain GamePlayer} implementation to use
     * @return Players assigned to their teams. <br>
     * Can be used to do intermediate operations on players based on their teams before <br>
     * actually assigning them, usually through {@linkplain TeamAssigner#assignTeams(Map)}.
     */
    public abstract <P extends GamePlayer> Map<GameTeam, Set<P>> assign(Game game,
                                                                        List<ITeamType> availableTeams,
                                                                        List<P> players);

    public static TeamAssigner simple(int teamSize) {
        return new TeamAssigner() {
            @Override public <P extends GamePlayer> Map<GameTeam, Set<P>> assign(Game game,
                                                                                 List<ITeamType> availableTeams,
                                                                                 List<P> players) {
                
                Map<GameTeam, Set<P>> assignedTeams = new HashMap<>();

                players = new ArrayList<>(players);

                for(ITeamType teamType : availableTeams) {
                    var team = Slime.getActiveGame().getTeamProvider().get();

                    team.setType(teamType);
                    team.setSize(teamSize);

                    Set<P> teamPlayers = new HashSet<>();
                    for(int i = 0; i < teamSize; i++) {
                        if(players.isEmpty()) {
                            //Add the team we're currently processing and exit
                            assignedTeams.put(team, teamPlayers);
                            return assignedTeams;
                        }

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
