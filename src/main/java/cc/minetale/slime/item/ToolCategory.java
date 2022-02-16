package cc.minetale.slime.item;

import cc.minetale.slime.item.Tiers.ToolTier;
import cc.minetale.slime.item.category.ITieredCategory;
import cc.minetale.slime.item.crafting.RecipePattern;
import cc.minetale.slime.item.enchant.EnchantmentCategory;
import cc.minetale.slime.item.enchant.IEnchantmentMarker;
import cc.minetale.slime.item.tier.ITierItem;
import cc.minetale.slime.item.tier.TieredItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minestom.server.gamedata.tags.Tag;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket.Ingredient;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static cc.minetale.slime.Slime.TAG_MANAGER;

@Getter @RequiredArgsConstructor
public final class ToolCategory implements ITieredCategory<TieredItem> {

    private static final Ingredient STICK_INGREDIENT = new Ingredient(Collections.singletonList(ItemStack.of(Material.STICK)));

    @Getter private static ToolCategory instance;

    private final TieredItem hoe;
    private final TieredItem pickaxe;
    private final TieredItem shovel;
    private final TieredItem axe;
    private final TieredItem sword; //TODO Move as separate item or weapons category

    private final NamespaceID id = NamespaceID.from("slime:category/tools");
    private final Set<TieredItem> items;

    public ToolCategory() {
        IEnchantmentMarker<Enchantment> commonPrimaryEnchants = IEnchantmentMarker.union(EnchantmentCategory.DIGGER,
                IEnchantmentMarker.of(Enchantment.UNBREAKING));

        IEnchantmentMarker<Enchantment> commonSecondaryEnchants = IEnchantmentMarker.union(EnchantmentCategory.VANISHABLE,
                IEnchantmentMarker.of(Enchantment.MENDING));

        IEnchantmentMarker<Enchantment> primaryEnchants = commonPrimaryEnchants;
        IEnchantmentMarker<Enchantment> secondaryEnchants = commonSecondaryEnchants;

        Map<Block, Float> perBlockMiningSpeedMultipliers = Collections.emptyMap();
        Set<Block> effectiveBlocks = getEffectiveBlocksFromTag("minecraft:mineable/hoe");

        this.hoe = new TieredItem(
                "hoe", NamespaceID.from("slime:tools/hoe"), Set.of(this),
                Collections.singletonList(RecipePattern.ofString(3, 3, "eiieseese")),
                Collections.singletonMap('s', STICK_INGREDIENT));

        Map<IToolTier, Set<ITierItem>> items = Map.ofEntries(
                Map.entry(ToolTier.WOOD, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:wooden_hoe"), this.hoe,
                                ToolTier.WOOD,
                                Material.WOODEN_HOE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.STONE, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:stone_hoe"), this.hoe,
                                ToolTier.STONE,
                                Material.STONE_HOE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.IRON, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:iron_hoe"), this.hoe,
                                ToolTier.IRON,
                                Material.IRON_HOE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.GOLD, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:golden_hoe"), this.hoe,
                                ToolTier.GOLD,
                                Material.GOLDEN_HOE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.DIAMOND, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:diamond_hoe"), this.hoe,
                                ToolTier.DIAMOND,
                                Material.DIAMOND_HOE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.NETHERITE, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:netherite_hoe"), this.hoe,
                                ToolTier.NETHERITE,
                                Material.NETHERITE_HOE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))));

        this.hoe.getTierItems().putAll(items);

        primaryEnchants = commonPrimaryEnchants;
        secondaryEnchants = commonSecondaryEnchants;

        effectiveBlocks = getEffectiveBlocksFromTag("minecraft:mineable/pickaxe");

        this.pickaxe = new TieredItem(
                "pickaxe", NamespaceID.from("slime:tools/pickaxe"), Set.of(this),
                Collections.singletonList(RecipePattern.ofString(3, 3, "iiieseese")),
                Collections.singletonMap('s', STICK_INGREDIENT));

        items = Map.ofEntries(
                Map.entry(ToolTier.WOOD, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:wooden_pickaxe"), this.pickaxe,
                                ToolTier.WOOD,
                                Material.WOODEN_PICKAXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.STONE, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:stone_pickaxe"), this.pickaxe,
                                ToolTier.STONE,
                                Material.STONE_PICKAXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.IRON, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:iron_pickaxe"), this.pickaxe,
                                ToolTier.IRON,
                                Material.IRON_PICKAXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.GOLD, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:golden_pickaxe"), this.pickaxe,
                                ToolTier.GOLD,
                                Material.GOLDEN_PICKAXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.DIAMOND, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:diamond_pickaxe"), this.pickaxe,
                                ToolTier.DIAMOND,
                                Material.DIAMOND_PICKAXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.NETHERITE, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:netherite_pickaxe"), this.pickaxe,
                                ToolTier.NETHERITE,
                                Material.NETHERITE_PICKAXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))));

        this.pickaxe.getTierItems().putAll(items);

        primaryEnchants = commonPrimaryEnchants;
        secondaryEnchants = commonSecondaryEnchants;

        effectiveBlocks = getEffectiveBlocksFromTag("minecraft:mineable/shovel");

        this.shovel = new TieredItem(
                "shovel", NamespaceID.from("slime:tools/shovel"), Set.of(this),
                Collections.singletonList(RecipePattern.ofString(3, 3, "eieeseese")),
                Collections.singletonMap('s', STICK_INGREDIENT));

        items = Map.ofEntries(
                Map.entry(ToolTier.WOOD, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:wooden_shovel"), this.shovel,
                                ToolTier.WOOD,
                                Material.WOODEN_SHOVEL,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.STONE, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:stone_shovel"), this.shovel,
                                ToolTier.STONE,
                                Material.STONE_SHOVEL,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.IRON, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:iron_shovel"), this.shovel,
                                ToolTier.IRON,
                                Material.IRON_SHOVEL,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.GOLD, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:golden_shovel"), this.shovel,
                                ToolTier.GOLD,
                                Material.GOLDEN_SHOVEL,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.DIAMOND, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:diamond_shovel"), this.shovel,
                                ToolTier.DIAMOND,
                                Material.DIAMOND_SHOVEL,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.NETHERITE, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:netherite_shovel"), this.shovel,
                                ToolTier.NETHERITE,
                                Material.NETHERITE_SHOVEL,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))));

        this.shovel.getTierItems().putAll(items);

        primaryEnchants = commonPrimaryEnchants;
        secondaryEnchants = IEnchantmentMarker.union(
                IEnchantmentMarker.of(Enchantment.SHARPNESS, Enchantment.SMITE, Enchantment.BANE_OF_ARTHROPODS),
                commonSecondaryEnchants);

        effectiveBlocks = getEffectiveBlocksFromTag("minecraft:mineable/axe");

        this.axe = new TieredItem(
                "axe", NamespaceID.from("slime:tools/axe"), Set.of(this),
                Collections.singletonList(RecipePattern.ofString(3, 3, "eiiesiese")),
                Collections.singletonMap('s', STICK_INGREDIENT));

        items = Map.ofEntries(
                Map.entry(ToolTier.WOOD, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:wooden_axe"), this.axe,
                                ToolTier.WOOD,
                                Material.WOODEN_AXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.STONE, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:stone_axe"), this.axe,
                                ToolTier.STONE,
                                Material.STONE_AXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.IRON, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:iron_axe"), this.axe,
                                ToolTier.IRON,
                                Material.IRON_AXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.GOLD, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:golden_axe"), this.axe,
                                ToolTier.GOLD,
                                Material.GOLDEN_AXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.DIAMOND, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:diamond_axe"), this.axe,
                                ToolTier.DIAMOND,
                                Material.DIAMOND_AXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.NETHERITE, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:netherite_axe"), this.axe,
                                ToolTier.NETHERITE,
                                Material.NETHERITE_AXE,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))));

        this.axe.getTierItems().putAll(items);

        primaryEnchants = IEnchantmentMarker.union(IEnchantmentMarker.of(Enchantment.UNBREAKING), EnchantmentCategory.WEAPON);
        secondaryEnchants = IEnchantmentMarker.union(commonSecondaryEnchants);

        effectiveBlocks = Collections.emptySet();

        this.sword = new TieredItem(
                "sword", NamespaceID.from("slime:tools/sword"), Set.of(this),
                Collections.singletonList(RecipePattern.ofString(3, 3, "eieeieese")),
                Collections.singletonMap('s', STICK_INGREDIENT));

        items = Map.ofEntries(
                Map.entry(ToolTier.WOOD, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:wooden_sword"), this.sword,
                                ToolTier.WOOD,
                                Material.WOODEN_SWORD,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.STONE, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:stone_sword"), this.sword,
                                ToolTier.STONE,
                                Material.STONE_SWORD,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.IRON, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:iron_sword"), this.sword,
                                ToolTier.IRON,
                                Material.IRON_SWORD,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.GOLD, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:golden_sword"), this.sword,
                                ToolTier.GOLD,
                                Material.GOLDEN_SWORD,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.DIAMOND, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:diamond_sword"), this.sword,
                                ToolTier.DIAMOND,
                                Material.DIAMOND_SWORD,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))),

                Map.entry(ToolTier.NETHERITE, Collections.singleton(
                        new TieredItem.Digger(NamespaceID.from("slime:netherite_sword"), this.sword,
                                ToolTier.NETHERITE,
                                Material.NETHERITE_SWORD,
                                primaryEnchants, secondaryEnchants,
                                perBlockMiningSpeedMultipliers, 1f,
                                effectiveBlocks))));

        this.sword.getTierItems().putAll(items);

        this.items = Set.of(
                this.hoe,
                this.pickaxe,
                this.shovel,
                this.axe,
                this.sword);

        instance = this;
    }

    @Override public Set<TieredItem> getAllItems() {
        return this.items;
    }

    private @NotNull Set<Block> getEffectiveBlocksFromTag(String namespace) {
        var tag = TAG_MANAGER.getTag(Tag.BasicType.BLOCKS, namespace);
        Set<Block> set = new HashSet<>();
        for(var id : tag.getValues()) {
            var block = Block.fromNamespaceId(id);
            if(block == null) { continue; }

            set.add(block);
        }
        return set;
    }
}
