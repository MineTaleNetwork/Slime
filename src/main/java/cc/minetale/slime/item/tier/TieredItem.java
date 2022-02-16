package cc.minetale.slime.item.tier;

import cc.minetale.slime.item.IArmorTier;
import cc.minetale.slime.item.ITier;
import cc.minetale.slime.item.IToolTier;
import cc.minetale.slime.item.base.IArmor;
import cc.minetale.slime.item.base.IDigger;
import cc.minetale.slime.item.category.ICategorizedTiered;
import cc.minetale.slime.item.category.ITieredCategory;
import cc.minetale.slime.item.crafting.RecipePattern;
import cc.minetale.slime.item.enchant.IEnchantable;
import cc.minetale.slime.item.enchant.IEnchantmentMarker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.EquipmentHandler;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket.Ingredient;
import net.minestom.server.utils.NamespaceID;

import java.util.*;

@Getter @Setter @AllArgsConstructor
public class TieredItem implements ICraftableTiered, ICategorizedTiered {
    private final String name;
    private final NamespaceID id;
    private final Set<ITieredCategory<? extends ICategorizedTiered>> parentCategories;

    private final Map<ITier, Set<ITierItem>> tierItems;

    private final List<RecipePattern> patterns;
    private final Map<Character, Ingredient> additionalIngredients;

    public TieredItem(String name, NamespaceID id,
                      Set<ITieredCategory<? extends ICategorizedTiered>> parentCategories,
                      List<RecipePattern> patterns, Map<Character, Ingredient> additionalIngredients) {

        this.name = name;
        this.id = id;
        this.parentCategories = parentCategories;
        this.patterns = patterns;
        this.additionalIngredients = additionalIngredients;

        this.tierItems = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public Map<ITier, Set<ITierItem>> getItemsGroupedByTier() {
        return this.tierItems;
    }

    @Override
    public Map<Character, Ingredient> getTierIngredients(ITier tier) {
        Map<Character, Ingredient> ingredients = ICraftableTiered.super.getTierIngredients(tier);
        ingredients.putAll(this.additionalIngredients);

        return ingredients;
    }

    @Getter @AllArgsConstructor
    public static class Digger implements ITierItem, IDigger, IEnchantable {
        private final NamespaceID id;

        private TieredItem parent;
        private final IToolTier tier;

        private final Material material;

        private IEnchantmentMarker<Enchantment> primaryEnchantmentMarker;
        private IEnchantmentMarker<Enchantment> secondaryEnchantmentMarker;

        private final Map<Block, Float> perBlockMiningSpeedMultipliers;
        private float miningSpeedMultiplier;

        private final Set<Block> effectiveBlocks;

        @Override
        public float getMiningSpeedMultiplierFor(Block block) {
            var miningSpeed = this.perBlockMiningSpeedMultipliers.get(block);
            if(miningSpeed != null) { return miningSpeed; }

            return this.miningSpeedMultiplier;
        }

        @Override
        public boolean canBreakBlock(Block block) {
            return !this.effectiveBlocks.contains(block);
        }

        @Override
        public int getEnchantability() {
            return this.tier.getEnchantability();
        }
    }

    @Getter @AllArgsConstructor
    public static class Armor implements ITierItem, IArmor, IEnchantable {
        private final NamespaceID id;

        private TieredItem parent;
        private final IArmorTier tier;

        private final Material material;

        private IEnchantmentMarker<Enchantment> primaryEnchantmentMarker;
        private IEnchantmentMarker<Enchantment> secondaryEnchantmentMarker;

        @Override
        public int getDurabilityMultiplier(Entity victim, DamageType type, float damage) {
            return this.tier.getDurabilityMultiplier();
        }

        @Override
        public Key getEquipSoundFor(EquipmentHandler wearer) {
            return this.tier.getEquipSound();
        }

        @Override
        public float getToughness(EquipmentHandler wearer) {
            return this.tier.getToughness();
        }

        @Override
        public float getKnockbackResistance(EquipmentHandler wearer) {
            return this.tier.getKnockbackResistance();
        }

        @Override
        public int getEnchantability() {
            return this.tier.getEnchantability();
        }
    }
}
