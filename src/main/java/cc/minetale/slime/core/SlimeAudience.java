package cc.minetale.slime.core;

import cc.minetale.slime.loadout.ILoadoutHolder;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.perceive.PerceiveAction;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.player.PlayerState;
import net.kyori.adventure.audience.Audience;

import java.util.function.Predicate;

public interface SlimeAudience extends Audience {
    default void setState(PlayerState state) {}

    default void perceive(GamePlayer target, PerceiveAction action) {}

    default void hideInTabIf(GamePlayer target, Predicate<GamePlayer> predicate) {}

    /** Check {@linkplain Loadout#setFor(ILoadoutHolder)} */
    default void setLoadout(Loadout loadout) {}

    /** Check {@linkplain Loadout#applyFor(ILoadoutHolder)} */
    default void applyLoadout(Loadout loadout) {}

    /** Check {@linkplain Loadout#replaceFor(ILoadoutHolder)} */
    default void replaceLoadout(Loadout loadout) {}
    default void removeLoadout() {}
}
