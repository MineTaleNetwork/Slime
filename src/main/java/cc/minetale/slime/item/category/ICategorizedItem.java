package cc.minetale.slime.item.category;

import cc.minetale.slime.item.base.IItem;

public interface ICategorizedItem extends IItem, ICategorized<ICategory<ICategorizedItem>> {
}
