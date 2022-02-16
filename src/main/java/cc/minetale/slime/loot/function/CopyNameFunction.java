package cc.minetale.slime.loot.function;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.LootPredicate;
import cc.minetale.slime.utils.TagUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter @Setter
public class CopyNameFunction extends LootFunction {
    private Source source;

    @JsonCreator
    protected CopyNameFunction(Source source, List<LootPredicate> conditions) {
        super(FunctionType.COPY_NAME, conditions);
        this.source = source;
    }

    @Override public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        if(source == Source.BLOCK_ENTITY && ctx instanceof LootContext.BlockCtx blockCtx) {
            var block = blockCtx.getBlock();
            return loot
                    .stream()
                    .map(itemStack -> TagUtil.copyDisplayName(block, itemStack))
                    .collect(Collectors.toList());
        }
        return loot;
    }

    public enum Source {
        BLOCK_ENTITY;

        @JsonValue
        private final String id = name().toLowerCase(Locale.ROOT);

        public String asId() {
            return this.id;
        }

        @JsonCreator
        public static Source fromId(NamespaceID id) {
            return Arrays.stream(Source.values())
                    .filter(type -> Objects.equals(type.id, id))
                    .findFirst()
                    .orElse(null);
        }
    }
}
