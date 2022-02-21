package cc.minetale.slime.loot.function;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.LootPredicate;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter @Setter
public class ExplorationMapFunction extends LootFunction {
    private String destination;
    private String decoration;
    private int zoom;
    private int searchRadius;
    private boolean skipExistingChunks;

    @JsonCreator
    protected ExplorationMapFunction(String destination, String decoration, int zoom, int searchRadius, boolean skipExistingChunks,
                                     List<LootPredicate> conditions) {

        super(FunctionType.EXPLORATION_MAP, conditions);
        this.destination = destination;
        this.decoration = decoration;
        this.zoom = zoom;
        this.searchRadius = searchRadius;
        this.skipExistingChunks = skipExistingChunks;
    }

    @Override
    public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        return loot
                .stream()
                .map(itemStack -> {
                    if(itemStack.getMaterial() != Material.MAP) { return itemStack; }

                    //TODO Requires locate functionality and no demand for this function yet
                    return itemStack;
                })
                .collect(Collectors.toList());
    }
}
