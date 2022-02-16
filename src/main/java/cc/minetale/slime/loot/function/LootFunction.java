package cc.minetale.slime.loot.function;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.entry.LootEntry;
import cc.minetale.slime.loot.predicate.LootPredicate;
import cc.minetale.slime.loot.util.NumberProvider;
import lombok.Getter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
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

    public static CopyNameFunction copyName(CopyNameFunction.Source source) {
        return new CopyNameFunction(source, null);
    }

    public static CopyNBTFunction copyNBT(CopyNBTFunction.Source source, List<CopyNBTFunction.Operation> operations) {
        return new CopyNBTFunction(source, operations, null);
    }

    public static CopyStateFunction copyState(NamespaceID block, List<String> properties) {
        return new CopyStateFunction(block, properties, null);
    }

    public static EnchantRandomlyFunction enchantRandomly(List<NamespaceID> enchantments) {
        return new EnchantRandomlyFunction(enchantments, null);
    }

    public static LootFunction enchantWithLevels() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootFunction explorationMap() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static ExplosionDecayFunction explosionDecay() {
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

    public static SetContentsFunction setContents(List<LootEntry> entries, NamespaceID type) {
        return new SetContentsFunction(entries, type, null);
    }

    public static SetCountFunction setCount(NumberProvider count, boolean add) {
        return new SetCountFunction(count, add, null);
    }

    public static SetDamageFunction setDamage(NumberProvider damage, boolean add) {
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

    public static LootFunction setPotion() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootFunction setStewEffect() {
        //TODO
        throw new UnsupportedOperationException();
    }
}
