package cc.minetale.slime.event.trait;

import cc.minetale.slime.loadout.Loadout;
import net.minestom.server.event.Event;

public interface LoadoutEvent extends Event {
    Loadout getLoadout();
}
