package cc.minetale.slime.core;

import cc.minetale.slime.loadout.ILoadoutHolder;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.player.IPlayerState;
import net.kyori.adventure.audience.Audience;

public interface SlimeAudience extends Audience {
    default void setState(IPlayerState state) {}

    /** Check {@linkplain Loadout#setFor(ILoadoutHolder)} */
    default void setLoadout(Loadout loadout) {}

    /** Check {@linkplain Loadout#applyFor(ILoadoutHolder)} */
    default void applyLoadout(Loadout loadout) {}

    /** Check {@linkplain Loadout#replaceFor(ILoadoutHolder)} */
    default void replaceLoadout(Loadout loadout) {}
    default void removeLoadout() {}
}
