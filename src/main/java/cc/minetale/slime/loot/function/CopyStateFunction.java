package cc.minetale.slime.loot.function;

import cc.minetale.commonlib.util.CollectionsUtil;
import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.LootPredicate;
import cc.minetale.slime.utils.TagUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
public class CopyStateFunction extends LootFunction {
    private NamespaceID block;
    private final List<String> properties;

    @JsonCreator
    protected CopyStateFunction(NamespaceID block, List<String> properties, List<LootPredicate> conditions) {
        super(FunctionType.COPY_STATE, conditions);
        this.block = block;

        this.properties = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(properties, Collections.emptyList())));
    }

    @Override
    public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        if(!(ctx instanceof LootContext.BlockCtx blockCtx)) { return loot; }

        var block = blockCtx.getBlock();
        if(!this.block.equals(block.namespace())) { return loot; }

        return loot
                .stream()
                .map(itemStack -> {
                    Map<String, String> requiredProperties = CollectionsUtil.subset(block.properties(), this.properties);
                    return TagUtil.Items.withState(itemStack, block, requiredProperties);
                })
                .collect(Collectors.toList());
    }
}
