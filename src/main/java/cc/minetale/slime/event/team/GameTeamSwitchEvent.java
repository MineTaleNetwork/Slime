package cc.minetale.slime.event.team;

import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.event.trait.GamePlayerEvent;
import cc.minetale.slime.team.GameTeam;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Called whenever a {@linkplain GamePlayer} switches teams.
 */
public class GameTeamSwitchEvent implements GameEvent, GamePlayerEvent {

    @Getter private Game game;
    @Getter private GamePlayer gamePlayer;

    @Getter private GameTeam previousTeam;
    @Getter private GameTeam nextTeam;

    public GameTeamSwitchEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer, @NotNull GameTeam previousTeam, @NotNull GameTeam nextTeam) {
        this.game = game;
        this.gamePlayer = gamePlayer;

        this.previousTeam = previousTeam;
        this.nextTeam = nextTeam;
    }

}
