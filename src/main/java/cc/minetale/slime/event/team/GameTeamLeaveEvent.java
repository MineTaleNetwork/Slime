package cc.minetale.slime.event.team;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import cc.minetale.slime.event.trait.GamePlayerEvent;
import cc.minetale.slime.event.trait.GameTeamEvent;
import cc.minetale.slime.team.GameTeam;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class GameTeamLeaveEvent implements GamePlayerEvent, GameTeamEvent {

    @Getter private Game game;
    @Getter private GamePlayer gamePlayer;

    @Getter private GameTeam team;

    public GameTeamLeaveEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer, @NotNull GameTeam team) {
        this.game = game;
        this.gamePlayer = gamePlayer;

        this.team = team;
    }

}
