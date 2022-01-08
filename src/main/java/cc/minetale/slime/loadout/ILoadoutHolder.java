package cc.minetale.slime.loadout;

import cc.minetale.slime.core.SlimeAudience;
import net.minestom.server.item.ItemStack;

import java.util.List;

/**
 * These methods should only set items and the current loadout. <br>
 * Any calls should be made to {@linkplain Loadout} or {@linkplain SlimeAudience} instead of these.
 */
public interface ILoadoutHolder {
    Loadout getLoadout();
    boolean hasLoadout();

    /** Check {@linkplain Loadout#applyFor(ILoadoutHolder)}. */
    boolean applyLoadout0(Loadout loadout, List<ItemStack> items);

    /** Check {@linkplain Loadout#replaceFor(ILoadoutHolder)}. */
    boolean replaceLoadout0(Loadout loadout, List<ItemStack> items);
    boolean removeLoadout0();
}
