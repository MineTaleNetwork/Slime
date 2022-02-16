package cc.minetale.slime.loot;

import cc.minetale.slime.loot.context.LootContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NoArgsConstructor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class LootTable {
    @JsonValue
    private Map<NamespaceID, Loot> raw = new ConcurrentHashMap<>();

    @JsonCreator
    private LootTable(Map<NamespaceID, Loot> raw) {
        this.raw = new ConcurrentHashMap<>(raw);
    }

    @SafeVarargs
    public static @NotNull LootTable ofEntries(Map.Entry<NamespaceID, Loot> @NotNull ... entries) {
        var lootTable = new LootTable();
        for(Map.Entry<NamespaceID, Loot> ent : entries) {
            lootTable.setLootFor(ent.getKey(), ent.getValue());
        }
        return lootTable;
    }

    public Loot getLootFor(NamespaceID id) {
        return this.raw.get(id);
    }

    public LootTable setLootFor(NamespaceID id, Loot loot) {
        this.raw.put(id, loot);
        return this;
    }

    public Loot removeLoot(NamespaceID id) {
        return this.raw.remove(id);
    }

    public List<ItemStack> generateLoot(LootContext ctx) {
        List<ItemStack> generated = new LinkedList<>();
        var loot = this.getLootFor(ctx.getId());
        if(loot == null) { return generated; }

        loot.generateLoot(generated, ctx);
        return generated;
    }

    public @UnmodifiableView Map<NamespaceID, Loot> getRawTable() {
        return Collections.unmodifiableMap(this.raw);
    }
}
