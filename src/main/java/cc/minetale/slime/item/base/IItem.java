package cc.minetale.slime.item.base;

import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;

public interface IItem {
    NamespaceID getId();
    Material getMaterial();
}
