package cc.minetale.slime.item;

import cc.minetale.slime.item.category.ITieredCategory;
import cc.minetale.slime.item.crafting.RecipePattern;
import cc.minetale.slime.item.enchant.EnchantmentCategory;
import cc.minetale.slime.item.enchant.IEnchantmentMarker;
import cc.minetale.slime.item.tier.ITierItem;
import cc.minetale.slime.item.tier.TieredItem;
import cc.minetale.slime.misc.restriction.RestrictionList;
import cc.minetale.slime.misc.restriction.Whitelist;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minestom.server.gamedata.tags.Tag;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static cc.minetale.slime.Slime.TAG_MANAGER;

@Getter @RequiredArgsConstructor
public final class ArmorCategory implements ITieredCategory<TieredItem> {

    @Getter private static ArmorCategory instance;

    private final TieredItem helmet;
    private final TieredItem chestplate;
    private final TieredItem leggings;
    private final TieredItem boots;

    private final NamespaceID id = NamespaceID.from("slime:category/armor");
    private final Set<TieredItem> items;

    public ArmorCategory() {
        IEnchantmentMarker<Enchantment> commonPrimaryEnchants = IEnchantmentMarker.union(EnchantmentCategory.ARMOR,
                IEnchantmentMarker.of(Enchantment.UNBREAKING));

        IEnchantmentMarker<Enchantment> commonSecondaryEnchants = IEnchantmentMarker.union(EnchantmentCategory.WEARABLE, EnchantmentCategory.VANISHABLE,
                IEnchantmentMarker.of(Enchantment.MENDING));

        IEnchantmentMarker<Enchantment> primaryEnchants = IEnchantmentMarker.union(EnchantmentCategory.ARMOR_HEAD, commonPrimaryEnchants);
        IEnchantmentMarker<Enchantment> secondaryEnchants = IEnchantmentMarker.union(EnchantmentCategory.ARMOR_CHEST, commonSecondaryEnchants);

        this.helmet = new TieredItem(
                "helmet", NamespaceID.from("slime:armor/helmet"), Set.of(this),
                Collections.singletonList(RecipePattern.ofString(3, 2, "iiiiei")),
                Collections.emptyMap());

        Map<IArmorTier, Set<ITierItem>> items = Map.ofEntries(
                Map.entry(Tiers.ArmorTier.LEATHER, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:leather_helmet"), this.helmet,
                                Tiers.ArmorTier.LEATHER,
                                Material.LEATHER_HELMET,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.GOLD, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:golden_helmet"), this.helmet,
                                Tiers.ArmorTier.GOLD,
                                Material.GOLDEN_HELMET,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.CHAINMAIL, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:chainmail_helmet"), this.helmet,
                                Tiers.ArmorTier.CHAINMAIL,
                                Material.CHAINMAIL_HELMET,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.IRON, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:iron_helmet"), this.helmet,
                                Tiers.ArmorTier.IRON,
                                Material.IRON_HELMET,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.TURTLE, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:turtle_helmet"), this.helmet,
                                Tiers.ArmorTier.TURTLE,
                                Material.TURTLE_HELMET,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.DIAMOND, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:diamond_helmet"), this.helmet,
                                Tiers.ArmorTier.DIAMOND,
                                Material.DIAMOND_HELMET,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.NETHERITE, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:netherite_helmet"), this.helmet,
                                Tiers.ArmorTier.NETHERITE,
                                Material.NETHERITE_HELMET,
                                primaryEnchants, secondaryEnchants))));

        this.helmet.getTierItems().putAll(items);

        primaryEnchants = IEnchantmentMarker.union(EnchantmentCategory.ARMOR_CHEST, commonPrimaryEnchants);
        secondaryEnchants = commonSecondaryEnchants;

        this.chestplate = new TieredItem(
                "chestplate", NamespaceID.from("slime:armor/chestplate"), Set.of(this),
                Collections.singletonList(RecipePattern.ofString(3, 3, "ieiiiiiii")),
                Collections.emptyMap());

        items = Map.ofEntries(
                Map.entry(Tiers.ArmorTier.LEATHER, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:leather_chestplate"), this.chestplate,
                                Tiers.ArmorTier.LEATHER,
                                Material.LEATHER_CHESTPLATE,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.GOLD, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:golden_chestplate"), this.chestplate,
                                Tiers.ArmorTier.GOLD,
                                Material.GOLDEN_CHESTPLATE,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.CHAINMAIL, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:chainmail_chestplate"), this.chestplate,
                                Tiers.ArmorTier.CHAINMAIL,
                                Material.CHAINMAIL_CHESTPLATE,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.IRON, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:iron_chestplate"), this.chestplate,
                                Tiers.ArmorTier.IRON,
                                Material.IRON_CHESTPLATE,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.DIAMOND, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:diamond_chestplate"), this.chestplate,
                                Tiers.ArmorTier.DIAMOND,
                                Material.DIAMOND_CHESTPLATE,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.NETHERITE, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:netherite_chestplate"), this.chestplate,
                                Tiers.ArmorTier.NETHERITE,
                                Material.NETHERITE_CHESTPLATE,
                                primaryEnchants, secondaryEnchants))));

        this.chestplate.getTierItems().putAll(items);

        primaryEnchants = commonPrimaryEnchants;
        secondaryEnchants = IEnchantmentMarker.union(EnchantmentCategory.ARMOR_CHEST, commonSecondaryEnchants);

        this.leggings = new TieredItem(
                "leggings", NamespaceID.from("slime:armor/leggings"), Set.of(this),
                Collections.singletonList(RecipePattern.ofString(3, 3, "iiiieiiei")),
                Collections.emptyMap());

        items = Map.ofEntries(
                Map.entry(Tiers.ArmorTier.LEATHER, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:leather_leggings"), this.leggings,
                                Tiers.ArmorTier.LEATHER,
                                Material.LEATHER_LEGGINGS,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.GOLD, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:golden_leggings"), this.leggings,
                                Tiers.ArmorTier.GOLD,
                                Material.GOLDEN_LEGGINGS,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.CHAINMAIL, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:chainmail_leggings"), this.leggings,
                                Tiers.ArmorTier.CHAINMAIL,
                                Material.CHAINMAIL_LEGGINGS,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.IRON, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:iron_leggings"), this.leggings,
                                Tiers.ArmorTier.IRON,
                                Material.IRON_LEGGINGS,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.DIAMOND, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:diamond_leggings"), this.leggings,
                                Tiers.ArmorTier.DIAMOND,
                                Material.DIAMOND_LEGGINGS,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.NETHERITE, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:netherite_leggings"), this.leggings,
                                Tiers.ArmorTier.NETHERITE,
                                Material.NETHERITE_LEGGINGS,
                                primaryEnchants, secondaryEnchants))));

        this.leggings.getTierItems().putAll(items);

        primaryEnchants = IEnchantmentMarker.union(IEnchantmentMarker.of(Enchantment.FEATHER_FALLING, Enchantment.DEPTH_STRIDER),
                commonPrimaryEnchants);

        secondaryEnchants = IEnchantmentMarker.union(EnchantmentCategory.ARMOR_CHEST, IEnchantmentMarker.of(Enchantment.FROST_WALKER),
                commonSecondaryEnchants);

        this.boots = new TieredItem(
                "boots", NamespaceID.from("slime:armor/boots"), Set.of(this),
                Collections.singletonList(RecipePattern.ofString(3, 2, "ieiiei")),
                Collections.emptyMap());

        items = Map.ofEntries(
                Map.entry(Tiers.ArmorTier.LEATHER, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:leather_boots"), this.boots,
                                Tiers.ArmorTier.LEATHER,
                                Material.LEATHER_BOOTS,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.GOLD, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:golden_boots"), this.boots,
                                Tiers.ArmorTier.GOLD,
                                Material.GOLDEN_BOOTS,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.CHAINMAIL, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:chainmail_boots"), this.boots,
                                Tiers.ArmorTier.CHAINMAIL,
                                Material.CHAINMAIL_BOOTS,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.IRON, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:iron_boots"), this.boots,
                                Tiers.ArmorTier.IRON,
                                Material.IRON_BOOTS,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.DIAMOND, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:diamond_boots"), this.boots,
                                Tiers.ArmorTier.DIAMOND,
                                Material.DIAMOND_BOOTS,
                                primaryEnchants, secondaryEnchants))),

                Map.entry(Tiers.ArmorTier.NETHERITE, Collections.singleton(
                        new TieredItem.Armor(NamespaceID.from("slime:netherite_boots"), this.boots,
                                Tiers.ArmorTier.NETHERITE,
                                Material.NETHERITE_BOOTS,
                                primaryEnchants, secondaryEnchants))));

        this.boots.getTierItems().putAll(items);

        this.items = Set.of(
                this.helmet,
                this.chestplate,
                this.leggings,
                this.boots);

        instance = this;
    }

    @Override public Set<TieredItem> getAllItems() {
        return this.items;
    }

    private @NotNull RestrictionList<Block> getEffectiveBlocksFromTag(String namespace) {
        var tag = TAG_MANAGER.getTag(Tag.BasicType.BLOCKS, namespace);
        Set<Block> set = new HashSet<>();
        for(var id : tag.getValues()) {
            var block = Block.fromNamespaceId(id);
            if(block == null) { continue; }

            set.add(block);
        }
        return Whitelist.of(set);
    }
}