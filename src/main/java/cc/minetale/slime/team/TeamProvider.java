package cc.minetale.slime.team;

import cc.minetale.slime.core.TeamStyle;
import cc.minetale.slime.game.Game;
import org.jetbrains.annotations.NotNull;

public interface TeamProvider {

    TeamProvider DEFAULT = new TeamProvider() {
        @Override
        public @NotNull GameTeam create(Game game, String id, int size, ITeamType type) {
            return new GameTeam(game, id, size, type);
        }

        @Override
        public @NotNull GameTeam createAnonymous(Game game, String id, int size) {
            return new GameTeam(game, id, size);
        }
    };

    @NotNull GameTeam create(Game game, String id, int size, ITeamType type);

    /** Refer to {@linkplain TeamStyle}. */
    @NotNull GameTeam createAnonymous(Game game, String id, int size);

}
