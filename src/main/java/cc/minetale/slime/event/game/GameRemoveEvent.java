package cc.minetale.slime.event.game;

import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.game.Game;
import lombok.Getter;

public class GameRemoveEvent implements GameEvent {

    @Getter private final Game game;

    public GameRemoveEvent(Game game) {
        this.game = game;
    }

}
