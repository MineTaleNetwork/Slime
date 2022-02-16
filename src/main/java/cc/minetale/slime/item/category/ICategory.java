package cc.minetale.slime.item.category;

import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;

import java.util.Set;

public interface ICategory<T extends ICategorized<?>> {
    NamespaceID getId();
    Set<T> getAllItems();
    Set<Material> getAllMaterials();
}
