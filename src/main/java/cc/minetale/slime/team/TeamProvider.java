package cc.minetale.slime.team;

import cc.minetale.slime.game.Game;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TeamProvider {

    TeamProvider DEFAULT = GameTeam::new;

    @NotNull GameTeam createTeam(Game game, String id, int size, ITeamType type);

}
