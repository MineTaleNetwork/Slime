package cc.minetale.slime.event.loadout;

import cc.minetale.slime.loadout.Loadout;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoadoutReplaceEvent implements PlayerEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private Player player;

    @Getter @Nullable private Loadout previousLoadout;
    @Getter @Setter @NotNull private Loadout newLoadout;

    public LoadoutReplaceEvent(@NotNull Player player, @Nullable Loadout previousLoadout, @NotNull Loadout newLoadout) {
        this.player = player;

        this.previousLoadout = previousLoadout;
        this.newLoadout = newLoadout;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
