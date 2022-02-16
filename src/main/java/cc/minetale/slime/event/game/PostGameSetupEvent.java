package cc.minetale.slime.event.game;

import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.GameInstance;
import cc.minetale.slime.game.Stage;
import lombok.Getter;

/**
 * This event should be listened to and is called during {@linkplain Stage#SETUP}.<br>
 * Its purpose is for a game to set up dynamic maps, for example set blocks, spawn entities, etc.
 */
public class PostGameSetupEvent implements GameEvent {

    @Getter private final Game game;
    @Getter private final GameInstance instance;

    public PostGameSetupEvent(Game game, GameInstance instance) {
        this.game = game;
        this.instance = instance;
    }

}
