package cc.minetale.slime.core;

import cc.minetale.slime.loadout.ILoadoutHolder;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.player.IPlayerState;
import net.kyori.adventure.audience.ForwardingAudience;
import org.jetbrains.annotations.NotNull;

public interface SlimeForwardingAudience extends ForwardingAudience, SlimeAudience {
    @Override
    @NotNull Iterable<? extends SlimeAudience> audiences();

    /** Check {@linkplain Loadout#setFor(ILoadoutHolder)} */
    default void setState(IPlayerState state) {
        for (final SlimeAudience audience : this.audiences()) audience.setState(state);
    }

    /** Check {@linkplain Loadout#setFor(ILoadoutHolder)} */
    default void setLoadout(Loadout loadout) {
        for (final SlimeAudience audience : this.audiences()) audience.setLoadout(loadout);
    }

    /** Check {@linkplain Loadout#applyFor(ILoadoutHolder)} */
    default void applyLoadout(Loadout loadout) {
        for (final SlimeAudience audience : this.audiences()) audience.applyLoadout(loadout);
    }

    /** Check {@linkplain Loadout#replaceFor(ILoadoutHolder)} */
    default void replaceLoadout(Loadout loadout) {
        for (final SlimeAudience audience : this.audiences()) audience.replaceLoadout(loadout);
    }

    default void removeLoadout() {
        for (final SlimeAudience audience : this.audiences()) audience.removeLoadout();
    }
}
