package cc.minetale.slime.core;

import cc.minetale.slime.loadout.ILoadoutHolder;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.perceive.IDelusory;
import cc.minetale.slime.perceive.PerceiveAction;
import cc.minetale.slime.perceive.PerceiveTeam;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.player.PlayerState;
import net.kyori.adventure.audience.ForwardingAudience;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface SlimeForwardingAudience extends ForwardingAudience, SlimeAudience {
    @Override
    @NotNull Iterable<? extends SlimeAudience> audiences();

    /** Check {@linkplain PlayerState} */
    default void setState(PlayerState state) {
        for (final SlimeAudience audience : this.audiences()) audience.setState(state);
    }

    /** Check {@linkplain PerceiveTeam} and {@linkplain PerceiveAction} */
    default void perceive(GamePlayer target, PerceiveAction action) {
        for (final SlimeAudience audience : this.audiences()) audience.perceive(target, action);
    }

    /** Check {@linkplain IDelusory} */
    default void hideInTabIf(GamePlayer target, Predicate<GamePlayer> predicate) {
        for (final SlimeAudience audience : this.audiences()) audience.hideInTabIf(target, predicate);
    }

    /** Check {@linkplain IDelusory} */
    default void hideInTab(GamePlayer target) {
        final Predicate<GamePlayer> alwaysHide = viewer -> true;
        for (final SlimeAudience audience : this.audiences()) audience.hideInTabIf(target, alwaysHide);
    }

    /** Check {@linkplain IDelusory} */
    default void showInTab(GamePlayer target) {
        final Predicate<GamePlayer> alwaysShow = viewer -> false;
        for (final SlimeAudience audience : this.audiences()) audience.hideInTabIf(target, alwaysShow);
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
