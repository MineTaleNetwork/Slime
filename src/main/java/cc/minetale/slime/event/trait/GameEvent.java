package cc.minetale.slime.event.trait;

import cc.minetale.slime.game.Game;
import net.minestom.server.event.Event;

public interface GameEvent extends Event {
    Game getGame();
}
