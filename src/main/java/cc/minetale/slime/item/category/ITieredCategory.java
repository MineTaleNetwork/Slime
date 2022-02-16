package cc.minetale.slime.item.category;

import cc.minetale.slime.item.ITier;
import cc.minetale.slime.item.tier.ITieredItem;
import net.minestom.server.item.Material;

import java.util.*;
import java.util.stream.Collectors;

public interface ITieredCategory<I extends ICategorizedTiered> extends ICategory<I> {
    default Map<ITier, Set<I>> getItemsGroupedByTier() {
        Map<ITier, Set<I>> result = new HashMap<>();
        for(I item : getAllItems()) {
            for(var tier : item.getPossibleTiers()) {
                result.compute(tier, (key, value) -> {
                    value = Objects.requireNonNullElse(value, new HashSet<>());
                    value.add(item);

                    return value;
                });
            }
        }
        return result;
    }

    default Map<ITier, Set<Material>> getMaterialsGroupedByTier() {
        return getItemsGroupedByTier().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(ITieredItem::getAllMaterials)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toSet())));
    }

    default Set<ITier> getAllPossibleTiers() {
        return getItemsGroupedByTier().keySet();
    }

    @Override
    default Set<I> getAllItems() {
        return getItemsGroupedByTier().values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    default Set<Material> getAllMaterials() {
        return getAllItems().stream()
                .map(ITieredItem::getAllMaterials)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
