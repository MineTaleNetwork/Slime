package cc.minetale.slime.item;

import lombok.Getter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.PotionMeta;
import net.minestom.server.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public enum ItemCategory {
    BUILDING(null, "Building Blocks", ItemStack.of(Material.BRICKS)),
    DECORATION(null, "Decoration Blocks", ItemStack.of(Material.PEONY)),
    REDSTONE(null, "Redstone", ItemStack.of(Material.REDSTONE)),
    TRANSPORTATION(null, "Transportation", ItemStack.of(Material.POWERED_RAIL)),
    MISCELLANEOUS(null, "Miscellaneous", ItemStack.of(Material.LAVA_BUCKET)),
    FOOD(null, "Foodstuffs", ItemStack.of(Material.APPLE)),
    TOOLS(null, "Tools", ItemStack.of(Material.IRON_AXE)),
    COMBAT(null, "Combat", ItemStack.of(Material.GOLDEN_SWORD)),
    SWORDS(COMBAT),
    ARMOR(COMBAT),
    BREWING(null, "Brewing", ItemStack.of(Material.POTION)
            .withMeta(meta -> {
                if(!(meta instanceof PotionMeta.Builder potionMeta)) { return meta; }
                potionMeta.potionType(PotionType.EMPTY);

                return potionMeta;
            }));

    @Getter private final ItemCategory parent;
    private final List<ItemCategory> children = Collections.synchronizedList(new ArrayList<>());

    @Getter private final String name;
    @Getter private final ItemStack displayItem;

    ItemCategory(@Nullable ItemCategory parent, @Nullable String name, @Nullable ItemStack displayItem) {
        if(parent != null)
            parent.children.add(this);

        this.parent = parent;

        this.name = name;
        this.displayItem = displayItem;
    }

    ItemCategory(ItemCategory parent) {
        this(parent, null, null);
    }

    public List<ItemCategory> getChildren() {
        return Collections.unmodifiableList(this.children);
    }
}
