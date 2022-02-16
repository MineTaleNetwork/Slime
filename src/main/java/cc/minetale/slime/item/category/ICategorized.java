package cc.minetale.slime.item.category;

import java.util.Set;

public interface ICategorized<C extends ICategory<?>> {
    Set<C> getParentCategories();
}
