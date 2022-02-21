package cc.minetale.slime.loot.function;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.entry.LootEntry;
import cc.minetale.slime.loot.predicate.LootPredicate;
import cc.minetale.slime.loot.util.NumberProvider;
import lombok.Getter;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.potion.PotionType;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

//TODO Make loot function a functional interface?
@Getter
public abstract class LootFunction {
    private final FunctionType function;

    private final List<LootPredicate> conditions;

    protected LootFunction(FunctionType function, List<LootPredicate> conditions) {
        this.function = function;

        this.conditions = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(conditions, Collections.emptyList())));
    }

    public abstract @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot);

    public LootFunction addCondition(LootPredicate condition) {
        this.conditions.add(condition);
        return this;
    }

    public LootFunction removeCondition(LootPredicate condition) {
        this.conditions.remove(condition);
        return this;
    }

    @Contract("_, _, _ -> new")
    public static @NotNull ApplyBonusFunction applyBonus(Enchantment enchantment, ApplyBonusFunction.Formula formula, ApplyBonusFunction.Parameters parameters) {
        return new ApplyBonusFunction(enchantment, formula, parameters, null);
    }

    @Contract("_ -> new")
    public static @NotNull CopyNameFunction copyName(CopyNameFunction.Source source) {
        return new CopyNameFunction(source, null);
    }

    @Contract("_, _ -> new")
    public static @NotNull CopyNBTFunction copyNBT(CopyNBTFunction.Source source, List<CopyNBTFunction.Operation> operations) {
        return new CopyNBTFunction(source, operations, null);
    }

    @Contract("_, _ -> new")
    public static @NotNull CopyStateFunction copyState(NamespaceID block, List<String> properties) {
        return new CopyStateFunction(block, properties, null);
    }

    @Contract("_ -> new")
    public static @NotNull EnchantRandomlyFunction enchantRandomly(List<NamespaceID> enchantments) {
        return new EnchantRandomlyFunction(enchantments, null);
    }

    public static @NotNull EnchantRandomlyFunction enchantRandomly() {
        return LootFunction.enchantRandomly(Collections.emptyList());
    }

    @Contract("_, _ -> new")
    public static @NotNull EnchantWithLevelsFunction enchantWithLevels(boolean treasure, NumberProvider levels) {
        return new EnchantWithLevelsFunction(treasure, levels, null);
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull ExplorationMapFunction explorationMap(String destination, String decoration, int zoom, int searchRadius, boolean skipExistingChunks) {
        return new ExplorationMapFunction(destination, decoration, zoom, searchRadius, skipExistingChunks, null);
    }

    @Contract(" -> new")
    public static @NotNull ExplosionDecayFunction explosionDecay() {
        return new ExplosionDecayFunction(null);
    }

    public static LootFunction furnaceSmelt() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootFunction fillPlayerHead() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootFunction limitCount() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootFunction lootingEnchant() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootFunction setAttributes() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootFunction setBannerPattern() {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Contract("_, _ -> new")
    public static @NotNull SetContentsFunction setContents(List<LootEntry> entries, NamespaceID type) {
        return new SetContentsFunction(entries, type, null);
    }

    @Contract("_, _ -> new")
    public static @NotNull SetCountFunction setCount(NumberProvider count, boolean add) {
        return new SetCountFunction(count, add, null);
    }

    @Contract("_, _ -> new")
    public static @NotNull SetDamageFunction setDamage(NumberProvider damage, boolean add) {
        return new SetDamageFunction(damage, add, null);
    }

    public static LootFunction setEnchantments() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootFunction setLootTable() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootFunction setLore() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootFunction setName() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootFunction setNBT() {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Contract("_ -> new")
    public static @NotNull SetPotionFunction setPotion(PotionType potionType) {
        return new SetPotionFunction(potionType, null);
    }

    @Contract("_ -> new")
    public static @NotNull LootFunction setStewEffect(List<SetStewEffectFunction.Effect> effects) {
        return new SetStewEffectFunction(effects, null);
    }
}
