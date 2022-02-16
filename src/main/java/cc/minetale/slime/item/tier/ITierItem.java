package cc.minetale.slime.item.tier;

import cc.minetale.slime.item.base.IItem;
import cc.minetale.slime.item.ITier;

public interface ITierItem extends IItem {
    ITieredItem getParent();
    ITier getTier();
}
