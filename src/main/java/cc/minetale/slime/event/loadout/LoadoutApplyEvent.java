package cc.minetale.slime.event.loadout;

import cc.minetale.slime.event.trait.LoadoutEvent;
import cc.minetale.slime.loadout.ILoadoutHolder;
import cc.minetale.slime.loadout.Loadout;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;
import org.jetbrains.annotations.NotNull;

public class LoadoutApplyEvent implements LoadoutEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private ILoadoutHolder holder;
    @Getter @Setter private Loadout loadout;

    public LoadoutApplyEvent(@NotNull ILoadoutHolder holder, @NotNull Loadout loadout) {
        this.holder = holder;
        this.loadout = loadout;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
