package cc.minetale.slime.event.player;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import cc.minetale.slime.event.trait.GamePlayerEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class GamePlayerLeaveEvent implements GamePlayerEvent {

    @Getter private Game game;
    @Getter private GamePlayer gamePlayer;

    public GamePlayerLeaveEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer) {
        this.game = game;
        this.gamePlayer = gamePlayer;
    }

}
