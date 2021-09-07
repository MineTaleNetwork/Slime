package cc.minetale.slime.event.trait;

import cc.minetale.slime.team.GameTeam;
import net.minestom.server.event.Event;

public interface GameEvent extends Event {
    GameTeam getGame();
}
