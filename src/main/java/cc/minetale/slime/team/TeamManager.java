package cc.minetale.slime.team;

import cc.minetale.slime.game.Game;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TeamManager {

    @Setter(AccessLevel.PACKAGE) private Game game;

    @Setter private TeamAssigner assigner;

    private final Map<String, GameTeam> teams = new ConcurrentHashMap<>();

    public <T extends GameTeam> T getTeam(String id) {
        return (T) this.teams.get(id);
    }

    public void addTeam(GameTeam team) {
        this.teams.put(team.getId(), team);
    }

    public void addTeams(Collection<GameTeam> teams) {
        teams.forEach(this::addTeam);
    }

    public void removeTeam(GameTeam team) {
        this.teams.remove(team.getId(), team);
    }

}
