package cc.minetale.slime.event.player;

import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.event.trait.GamePlayerEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class GamePlayerLeaveEvent implements GameEvent, GamePlayerEvent {

    @Getter private Game game;
    @Getter private GamePlayer gamePlayer;

    public GamePlayerLeaveEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer) {
        this.game = game;
        this.gamePlayer = gamePlayer;
    }

}
