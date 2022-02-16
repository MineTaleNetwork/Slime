package cc.minetale.slime.item;

import cc.minetale.slime.item.marker.ITieredMarker;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.Material;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Tiers {
    @Getter @RequiredArgsConstructor
    public enum ToolTier implements IToolTier {
        WOOD("wood", "wooden", 1,
                getDurabilityFromMaterial(Material.WOODEN_PICKAXE), 2f, 0f, 15,
                new HashSet<>()),

        STONE("stone", "stone", 2,
                getDurabilityFromMaterial(Material.STONE_PICKAXE), 4f, 1f, 5,
                new HashSet<>()),

        IRON("iron", "iron", 3,
                getDurabilityFromMaterial(Material.IRON_PICKAXE), 6f, 2f, 14,
                new HashSet<>(Collections.singleton(Material.IRON_INGOT))),

        GOLD("gold", "golden", 4,
                getDurabilityFromMaterial(Material.GOLDEN_PICKAXE), 12f, 3f, 22,
                new HashSet<>(Collections.singleton(Material.GOLD_INGOT))),

        DIAMOND("diamond", "diamond", 5,
                getDurabilityFromMaterial(Material.DIAMOND_PICKAXE), 8f, 0f, 10,
                new HashSet<>(Collections.singleton(Material.DIAMOND))),

        NETHERITE("netherite", "netherite", 6,
                getDurabilityFromMaterial(Material.NETHERITE_PICKAXE), 9f, 4f, 15,
                new HashSet<>(Collections.singleton(Material.NETHERITE_INGOT)));

        private final String noun;
        private final String adjective;
        private final int level;

        //TODO Related tiers?

        private final int durability;
        private final float miningSpeed;

        private final float damage;

        private final int enchantability;

        @Setter(AccessLevel.PACKAGE) private ITieredMarker<?> applicableItems;
        private final Set<Material> materials;

        private static int getDurabilityFromMaterial(Material material) {
            return material.registry().maxDamage();
        }
    }

    @Getter @RequiredArgsConstructor
    public enum ArmorTier implements IArmorTier {
        LEATHER("leather", "leather", 1,
                5, List.of(1, 2, 3, 1), 15, Key.key("entity.experience_orb.pickup"), 0f, 0f,
                new HashSet<>(Collections.singleton(Material.LEATHER))),

        GOLD("gold", "golden", 2,
                7, List.of(2, 5, 6, 2), 9, Key.key("entity.experience_orb.pickup"), 0f, 0f,
                new HashSet<>(Collections.singleton(Material.GOLD_INGOT))),

        CHAINMAIL("chainmail", "chainmail", 3,
                15, List.of(1, 4, 5, 2), 12, Key.key("entity.experience_orb.pickup"), 0f, 0f,
                new HashSet<>()),

        IRON("iron", "iron", 4,
                15, List.of(2, 5, 6, 2), 9, Key.key("entity.experience_orb.pickup"), 0f, 0f,
                new HashSet<>(Collections.singleton(Material.IRON_INGOT))),

        TURTLE("turtle", "turtle", 5,
                25, List.of(2, 5, 6, 2), 9, Key.key("entity.experience_orb.pickup"), 0f, 0f,
                new HashSet<>()),

        DIAMOND("diamond", "diamond", 5,
                33, List.of(3, 6, 8, 3), 10, Key.key("entity.experience_orb.pickup"), 0f, 0f,
                new HashSet<>(Collections.singleton(Material.DIAMOND))),

        NETHERITE("netherite", "netherite", 6,
                37, List.of(3, 6, 8, 3), 15, Key.key("entity.experience_orb.pickup"), 3f, .1f,
                new HashSet<>(Collections.singleton(Material.NETHERITE_INGOT)));

        private final String noun;
        private final String adjective;
        private final int level;

        //TODO Related tiers?

        private final int durabilityMultiplier;
        private final List<Integer> protectionAmounts;

        private final int enchantability;

        private final Key equipSound;

        private final float toughness;
        private final float knockbackResistance;

        @Setter(AccessLevel.PACKAGE) private ITieredMarker<?> applicableItems;
        private final Set<Material> materials;
    }
}
