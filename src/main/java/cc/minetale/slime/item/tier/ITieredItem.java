package cc.minetale.slime.item.tier;

import cc.minetale.slime.item.ITier;
import cc.minetale.slime.item.base.IItem;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface ITieredItem {
    String getName();
    NamespaceID getId();

    Map<ITier, Set<ITierItem>> getItemsGroupedByTier();

    default Set<ITierItem> getItemsByTier(ITier tier) {
        return getItemsGroupedByTier().get(tier);
    }

    default ITier getTierFromItem(IItem item) {
        for(var ent : getItemsGroupedByTier().entrySet()) {
            var items = ent.getValue();
            for(var otherItem : items) {
                var id = otherItem.getId();
                if(id.equals(item.getId()))
                    return ent.getKey();
            }
        }
        return null;
    }

    default Set<ITier> getPossibleTiers() {
        return getItemsGroupedByTier().keySet();
    }

    default Set<IItem> getAllItems() {
        return getItemsGroupedByTier().values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    default Set<Material> getAllMaterials() {
        return getAllItems().stream()
                .map(IItem::getMaterial)
                .collect(Collectors.toSet());
    }
}
