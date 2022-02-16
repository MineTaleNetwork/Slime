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
public class PredicateBlock {
    private final List<NamespaceID> blocks;
    private @Nullable String tag;
    private @Nullable String nbt;
    private final Map<String, String> state; //Documentation is confusing, is this correct?

    public PredicateBlock(@Nullable List<NamespaceID> blocks,
                          @Nullable String tag,
                          @Nullable String nbt,
                          @Nullable Map<String, String> state) {

        this.tag = tag;
        this.nbt = nbt;

        this.blocks = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(blocks, Collections.emptyList())));

        this.state = Collections.synchronizedMap(
                new HashMap<>(Objects.requireNonNullElse(state, Collections.emptyMap())));
    }

    public boolean test(Block block) {
        var id = block.namespace();
        if(!this.blocks.contains(id))
            return false;

        if(this.tag != null) {
            var tag = TAG_MANAGER.getTag(Tag.BasicType.BLOCKS, this.tag);
            if(tag == null || !tag.contains(id)) { return false; }
        }

        if(this.nbt != null) {
            var snbt = block.getTag(net.minestom.server.tag.Tag.SNBT);
            if(snbt == null || this.nbt.equals(snbt)) { return false; }
        }

        return this.state.entrySet()
                .stream()
                .allMatch(requiredState -> {
                    var property = requiredState.getKey();
                    var expected = requiredState.getValue();
                    return expected.equals(block.getProperty(property));
                });
    }
}
