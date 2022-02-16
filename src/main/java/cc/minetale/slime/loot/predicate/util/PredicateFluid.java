package cc.minetale.slime.loot.predicate.util;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.gamedata.tags.Tag;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static cc.minetale.slime.Slime.TAG_MANAGER;

@Getter @Setter
public class PredicateFluid {
    private @Nullable NamespaceID fluid;
    private @Nullable String tag;
    private final Map<String, String> state; //Documentation is confusing, is this correct?

    public PredicateFluid(@Nullable NamespaceID fluid,
                          @Nullable String tag,
                          Map<String, String> state) {

        this.fluid = fluid;
        this.tag = tag;

        this.state = Collections.synchronizedMap(
                new HashMap<>(Objects.requireNonNullElse(state, Collections.emptyMap())));
    }

    public boolean test(Block block) {
        if(!block.isLiquid()) { return false; }

        if(this.fluid != null && !this.fluid.equals(block.namespace())) { return false; }

        if(this.tag != null) {
            var tag = TAG_MANAGER.getTag(Tag.BasicType.FLUIDS, this.tag);
            if(tag == null) { return false; }
        }

        if(this.state != null) {
            return !this.state.entrySet()
                    .stream()
                    .allMatch(requiredState -> {
                        var currentState = block.getProperty(requiredState.getKey());
                        return currentState.equals(requiredState.getValue());
                    });
        }

        return true;
    }
}
