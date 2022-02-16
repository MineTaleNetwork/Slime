package cc.minetale.slime.loot;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum TableType {
    BLOCK("loot_tables/block_loot_tables.json"),
    CHEST("loot_tables/chest_loot_tables.json"),
    ENTITY("loot_tables/entity_loot_tables.json"),
    GAMEPLAY("loot_tables/gameplay_loot_tables.json");

    private final String name;
}
