package cc.minetale.slime.event.game;

import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.Stage;
import lombok.Getter;

/**
 * This event can be listened to and is the last event called by Slime during {@linkplain Stage#SETUP}, after {@linkplain PostInstanceSetupEvent}.<br>
 */
public class PostGameSetupEvent implements GameEvent {

    @Getter private final Game game;

    public PostGameSetupEvent(Game game) {
        this.game = game;
    }

}
