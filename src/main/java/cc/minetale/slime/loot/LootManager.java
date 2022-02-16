package cc.minetale.slime.loot;

import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class LootManager {
    private final Map<NamespaceID, LootTable> tables = new HashMap<>();

    public @Nullable LootTable registerTable(NamespaceID id, LootTable table) {
        synchronized (this.tables) {
            return this.tables.put(id, table);
        }
    }

    public LootTable getTable(NamespaceID id) {
        return this.tables.get(id);
    }
}
