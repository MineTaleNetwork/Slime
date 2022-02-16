package cc.minetale.slime.loot.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

@Getter @Setter @AllArgsConstructor
public abstract class LootContext {
    private NamespaceID id;
    @Nullable protected Instance instance;
    @Nullable protected Point pos;

    public static class Empty extends LootContext {
        public Empty(NamespaceID id) {
            super(id, null, null);
        }
    }

    @Getter @Setter
    public static class ChestCtx extends LootContext {
        private @Nullable Entity openingEntity;

        public ChestCtx(NamespaceID id, Instance instance, Vec pos, @Nullable Entity openingEntity) {
            super(id, instance, pos);
            this.openingEntity = openingEntity;
        }
    }

    @Getter @Setter
    public static class CommandCtx extends LootContext {
        private @Nullable Entity executor;

        public CommandCtx(NamespaceID id, Instance instance, Vec pos, @Nullable Entity executor) {
            super(id, instance, pos);
            this.executor = executor;
        }
    }

    @Getter @Setter
    public static class SelectorCtx extends LootContext {
        private Entity entity;

        public SelectorCtx(NamespaceID id, Instance instance, Vec pos, @Nullable Entity entity) {
            super(id, instance, pos);
            this.entity = entity;
        }
    }

    @Getter @Setter
    public static class FishingCtx extends LootContext {
        private ItemStack fishingRod;
        private Entity hook;

        public FishingCtx(NamespaceID id, Instance instance, Vec hookPos, ItemStack fishingRod, Entity hook) {
            super(id, instance, hookPos);
            this.fishingRod = fishingRod;
            this.hook = hook;
        }
    }

    @Getter @Setter
    public static class EntityCtx extends LootContext {
        private Entity entity;
        private DamageType damageSource;

        //Documentation strikes again, which one is what exactly?
        private @Nullable Entity killer;
        private @Nullable Entity directKiller;
        private @Nullable Player lastDamager;

        public EntityCtx(NamespaceID id, Entity entity, DamageType damageSource,
                         @Nullable Entity killer, @Nullable Entity directKiller, @Nullable Player lastDamager) {

            super(id, entity.getInstance(), entity.getPosition());
            this.entity = entity;
            this.damageSource = damageSource;
            this.killer = killer;
            this.directKiller = directKiller;
            this.lastDamager = lastDamager;
        }

        public EntityCtx(NamespaceID id, Entity entity, DamageType damageSource, @Nullable Entity killer, @Nullable Entity directKiller) {
            this(id, entity, damageSource, killer, directKiller, null);
        }

        public EntityCtx(NamespaceID id, Entity entity, DamageType damageSource, @Nullable Entity killer) {
            this(id, entity, damageSource, killer, null, null);
        }

        public EntityCtx(NamespaceID id, Entity entity, DamageType damageSource) {
            this(id, entity, damageSource, null, null, null);
        }
    }

    @Getter @Setter
    public static class GiftCtx extends LootContext {
        private Entity gift;

        public GiftCtx(NamespaceID id, Entity gift) {
            super(id, gift.getInstance(), gift.getPosition());
            this.gift = gift;
        }
    }

    @Getter @Setter
    public static class BarterCtx extends LootContext {
        private Entity piglin;

        public BarterCtx(NamespaceID id, Entity piglin) {
            super(id, piglin.getInstance(), piglin.getPosition());
            this.piglin = piglin;
        }
    }

    @Getter @Setter
    public static class AdvancementRewardCtx extends LootContext {
        private Player player;

        public AdvancementRewardCtx(NamespaceID id, Player player) {
            super(id, player.getInstance(), player.getPosition());
            this.player = player;
        }
    }

    @Getter @Setter
    public static class AdvancementEntityCtx extends LootContext {
        private Entity entity;

        public AdvancementEntityCtx(NamespaceID id, Entity entity) {
            super(id, entity.getInstance(), entity.getPosition());
            this.entity = entity;
        }
    }

    @Getter @Setter
    public static class GenericCtx extends LootContext {
        //Documentation confusing, what is this for?

        public GenericCtx(NamespaceID id) {
            super(id, null, null);
        }
    }

    @Getter @Setter
    public static class BlockCtx extends LootContext {
        private Block block;
        private ItemStack tool;

        private @Nullable Entity breaker;
        private @Nullable Integer radius;

        private BlockCtx(NamespaceID id, Instance instance, Point pos, Block block, ItemStack tool,
                         @Nullable Entity breaker, @Nullable Integer radius) {

            super(id, instance, pos);
            this.block = block;
            this.tool = tool;
            this.breaker = breaker;
            this.radius = radius;
        }

        public BlockCtx(NamespaceID id, Instance instance, Point pos, Block block, ItemStack tool, Entity breaker) {
            this(id, instance, pos, block, tool, breaker, null);
        }

        public BlockCtx(NamespaceID id, Instance instance, Point pos, Block block, ItemStack tool, Integer radius) {
            this(id, instance, pos, block, tool, null, radius);
        }
    }
}
