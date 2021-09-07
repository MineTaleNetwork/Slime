package cc.minetale.slime.event.loadout;

import cc.minetale.slime.event.trait.LoadoutEvent;
import cc.minetale.slime.loadout.Loadout;
import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class LoadoutRemoveEvent implements LoadoutEvent, PlayerEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private Player player;
    @Getter private Loadout loadout;

    public LoadoutRemoveEvent(@NotNull Player player, @NotNull Loadout loadout) {
        this.player = player;
        this.loadout = loadout;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
