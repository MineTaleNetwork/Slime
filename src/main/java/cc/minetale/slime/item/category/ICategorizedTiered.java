package cc.minetale.slime.item.category;

import cc.minetale.slime.item.tier.ITieredItem;

public interface ICategorizedTiered extends ITieredItem, ICategorized<ITieredCategory<? extends ICategorizedTiered>> {
}
