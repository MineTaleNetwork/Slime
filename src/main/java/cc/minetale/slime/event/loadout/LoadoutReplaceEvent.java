package cc.minetale.slime.event.loadout;

import cc.minetale.slime.loadout.ILoadoutHolder;
import cc.minetale.slime.loadout.Loadout;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoadoutReplaceEvent implements CancellableEvent {

    private boolean cancelled;

    @Getter private ILoadoutHolder holder;

    @Getter @Nullable private Loadout previousLoadout;
    @Getter @Setter @NotNull private Loadout newLoadout;

    public LoadoutReplaceEvent(@NotNull ILoadoutHolder holder, @Nullable Loadout previousLoadout, @NotNull Loadout newLoadout) {
        this.holder = holder;

        this.previousLoadout = previousLoadout;
        this.newLoadout = newLoadout;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
