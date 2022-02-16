package cc.minetale.slime.loot.predicate;

import cc.minetale.slime.loot.context.LootContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.utils.NamespaceID;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter @Setter
public class BlockStatePropertyPredicate extends LootPredicate {
    private NamespaceID block;
    private final Map<String, String> properties;

    @JsonCreator
    protected BlockStatePropertyPredicate(NamespaceID block, Map<String, String> properties) {
        super(PredicateType.BLOCK_STATE_PROPERTY);
        this.block = block;

        this.properties = Collections.synchronizedMap(
                new HashMap<>(Objects.requireNonNullElse(properties, Collections.emptyMap())));
    }

    @Override
    public boolean test(LootContext ctx) {
        if(!(ctx instanceof LootContext.BlockCtx blockContext)) { return false; }

        var contextBlock = blockContext.getBlock();
        if(!this.block.equals(contextBlock.namespace())) { return false; }

        for(Map.Entry<String, String> ent : contextBlock.properties().entrySet()) {
            final var key = ent.getKey();
            final var value = ent.getValue();

            var expected = this.properties.get(key);
            if(expected != null && !expected.equals(value)) {
                return false;
            }
        }

        return true;
    }
}
