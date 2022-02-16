package cc.minetale.slime.loot.predicate.util;

import cc.minetale.slime.loot.util.IntegerRangeProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.AgeableMobMeta;
import net.minestom.server.entity.metadata.other.FishingHookMeta;
import net.minestom.server.entity.metadata.other.LightningBoltMeta;
import net.minestom.server.inventory.EquipmentHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

@Getter @Setter
public class PredicateEntityProps {
    private @Nullable PredicateDistance distance;
    private final Map<NamespaceID, Effect> effects;
    private @Nullable Equipment equipment;
    private @Nullable Flags flags;
    private @Nullable LightningBolt lightningBolt;
    private @Nullable PredicateLocation location;
    private @Nullable String nbt;
    private @Nullable PredicateEntityProps passenger;
    private @Nullable PredicatePlayerProps player;
    private @Nullable PredicateLocation steppingOn;
    private @Nullable String team;
    private @Nullable NamespaceID type;
    private @Nullable PredicateEntityProps targetedEntity;
    private @Nullable PredicateEntityProps vehicle;
    private @Nullable FishingHook fishingHook;

    //TODO Factory methods, make this private
    public PredicateEntityProps(@Nullable PredicateDistance distance,
                                Map<NamespaceID, Effect> effects,
                                @Nullable Equipment equipment,
                                @Nullable Flags flags,
                                @Nullable LightningBolt lightningBolt,
                                @Nullable PredicateLocation location,
                                @Nullable String nbt,
                                @Nullable PredicateEntityProps passenger,
                                @Nullable PredicatePlayerProps player,
                                @Nullable PredicateLocation steppingOn,
                                @Nullable String team,
                                @Nullable NamespaceID type,
                                @Nullable PredicateEntityProps targetedEntity,
                                @Nullable PredicateEntityProps vehicle,
                                @Nullable FishingHook fishingHook) {

        this.distance = distance;
        this.equipment = equipment;
        this.flags = flags;
        this.lightningBolt = lightningBolt;
        this.location = location;
        this.nbt = nbt;
        this.passenger = passenger;
        this.player = player;
        this.steppingOn = steppingOn;
        this.team = team;
        this.type = type;
        this.targetedEntity = targetedEntity;
        this.vehicle = vehicle;
        this.fishingHook = fishingHook;

        this.effects = Collections.synchronizedMap(
                new HashMap<>(Objects.requireNonNullElse(effects, Collections.emptyMap())));
    }

    public boolean test(Entity entity, @Nullable Entity killer) {
        final var pos = entity.getPosition().asVec();
        if(this.distance != null && (killer != null && !this.distance.test(pos, killer.getPosition())))
            return false;

        if(this.effects != null) {
            if(!this.effects.entrySet()
                    .stream()
                    .allMatch(ent -> {
                        final var id = ent.getKey();
                        final var effect = ent.getValue();
                        return effect.test(id, entity);
                    })) {

                return false;
            }
        }

        if(this.equipment != null && (!(entity instanceof EquipmentHandler equipmentHandler) || !this.equipment.test(equipmentHandler)))
            return false;

        if(this.flags != null && !this.flags.test(entity))
            return false;

        if(this.lightningBolt != null && !this.lightningBolt.test(entity))
            return false;

        if(this.location != null) {
            var instance = entity.getInstance();
            if(instance == null || !this.location.test(instance, pos)) { return false; }
        }

        if(this.nbt != null) {
            var nbt = entity.getTag(Tag.SNBT);
            if(!this.nbt.equals(nbt)) { return false; }
        }

        if(this.passenger != null) {
            var passengers = entity.getPassengers();
            if(!passengers
                    .stream()
                    .allMatch(passenger -> this.passenger.test(passenger, null))) {

                return false;
            }
        }

        if(this.player != null && (!(entity instanceof Player player) || !this.player.test(player)))
            return false;

        if(this.steppingOn != null) {
            var instance = entity.getInstance();
            var position = entity.getPosition().asVec();
            if(!this.steppingOn.test(instance, position)) { return false; }
        }

        if(this.team != null) {
            if(!(entity instanceof LivingEntity livingEntity)) { return false; }

            var team = livingEntity.getTeam();
            if(!this.team.equals(team.getTeamName())) { return false; }
        }

        if(this.type != null && this.type != entity.getEntityType().namespace())
            return false;

        if(this.targetedEntity != null) {
            var targetedEntity = entity.getLineOfSightEntity(64, null);
            if(targetedEntity == null || !this.targetedEntity.test(targetedEntity, null)) { return false; }
        }

        if(this.vehicle != null) {
            var vehicle = entity.getVehicle();
            if(vehicle == null || !this.vehicle.test(vehicle, null)) { return false; }
        }

        if(this.fishingHook != null) {
            if(!(entity.getEntityMeta() instanceof FishingHookMeta meta)) { return false; }
            //TODO
        }

        return true;
    }

    public @UnmodifiableView Map<NamespaceID, Effect> getEffects() {
        return Collections.unmodifiableMap(this.effects);
    }

    @Getter @NoArgsConstructor
    public static class Effect {
        private @Nullable Boolean ambient;
        private @Nullable IntegerRangeProvider amplifier;
        private @Nullable IntegerRangeProvider duration;
        private @Nullable Boolean visible;

        //TODO Factory methods

        public boolean test(NamespaceID effectId, Entity entity) {
            return entity.getActiveEffects()
                    .stream()
                    .anyMatch(effect -> {
                        final var potion = effect.getPotion();
                        if(potion.effect().namespace() != effectId)
                            return false;

                        return (ambient == null || ambient == potion.isAmbient()) &&
                                (amplifier == null || amplifier.isInRange(potion.amplifier())) &&
                                (duration == null || duration.isInRange(potion.duration())) &&
                                (visible == null || visible == potion.hasParticles());
                    });
        }

        @Contract(pure = true)
        public Effect requireAmbient(@Nullable Boolean ambient) {
            this.ambient = ambient;
            return this;
        }

        @Contract(pure = true)
        public Effect requireAmplifier(@Nullable IntegerRangeProvider range) {
            this.amplifier = range;
            return this;
        }

        @Contract(pure = true)
        public Effect requireDuration(@Nullable IntegerRangeProvider range) {
            this.duration = range;
            return this;
        }

        @Contract(pure = true)
        public Effect requireVisibility(@Nullable Boolean visible) {
            this.visible = visible;
            return this;
        }
    }

    @Getter @NoArgsConstructor
    public static class Equipment {
        private @Nullable PredicateItem mainhand;
        private @Nullable PredicateItem offhand;
        @JsonProperty("head") private @Nullable PredicateItem helmet;
        @JsonProperty("chest") private @Nullable PredicateItem chestplate;
        @JsonProperty("legs") private @Nullable PredicateItem leggings;
        @JsonProperty("feet") private @Nullable PredicateItem boots;

        //TODO Factory methods

        public boolean test(EquipmentHandler handler) {
            return (this.mainhand == null || this.mainhand.test(handler.getItemInMainHand())) &&
                    (this.offhand == null || this.offhand.test(handler.getItemInOffHand())) &&
                    (this.helmet == null || this.helmet.test(handler.getHelmet())) &&
                    (this.chestplate == null || this.chestplate.test(handler.getChestplate())) &&
                    (this.leggings == null || this.leggings.test(handler.getLeggings())) &&
                    (this.boots == null || this.boots.test(handler.getBoots()));
        }

        @Contract(pure = true)
        public Equipment requireMainhand(@Nullable PredicateItem mainhand) {
            this.mainhand = mainhand;
            return this;
        }

        @Contract(pure = true)
        public Equipment requireOffhand(@Nullable PredicateItem offhand) {
            this.offhand = offhand;
            return this;
        }

        @Contract(pure = true)
        public Equipment requireHelmet(@Nullable PredicateItem helmet) {
            this.helmet = helmet;
            return this;
        }

        @Contract(pure = true)
        public Equipment requireChestplate(@Nullable PredicateItem chestplate) {
            this.chestplate = chestplate;
            return this;
        }

        @Contract(pure = true)
        public Equipment requireLeggings(@Nullable PredicateItem leggings) {
            this.leggings = leggings;
            return this;
        }

        @Contract(pure = true)
        public Equipment requireBoots(@Nullable PredicateItem boots) {
            this.boots = boots;
            return this;
        }
    }

    @Getter @NoArgsConstructor
    public static class Flags {
        private @Nullable Boolean isOnFire;
        private @Nullable Boolean isSneaking;
        private @Nullable Boolean isSwimming;
        private @Nullable Boolean isBaby;

        //TODO Factory methods

        public boolean test(Entity entity) {
            final var meta = entity.getEntityMeta();
            return (this.isOnFire == null || this.isOnFire == meta.isOnFire()) &&
                    (this.isSneaking == null || this.isSneaking == meta.isSneaking()) &&
                    (this.isSwimming == null || this.isSwimming == meta.isSwimming()) &&
                    (this.isBaby == null || (meta instanceof AgeableMobMeta ageableMeta && this.isBaby == ageableMeta.isBaby()));
        }

        @Contract(pure = true)
        public Flags requireOnFire(@Nullable Boolean status) {
            this.isOnFire = status;
            return this;
        }

        @Contract(pure = true)
        public Flags requireSneaking(@Nullable Boolean status) {
            this.isSneaking = status;
            return this;
        }

        @Contract(pure = true)
        public Flags requireSwimming(@Nullable Boolean status) {
            this.isSwimming = status;
            return this;
        }

        @Contract(pure = true)
        public Flags requireAge(@Nullable Boolean isBaby) {
            this.isBaby = isBaby;
            return this;
        }
    }

    @Getter @NoArgsConstructor
    public static class LightningBolt {
        private @Nullable Integer blocksSetOnFire;
        private @Nullable PredicateEntityProps entityStruck;

        //TODO Factory methods

        public boolean test(Entity entity) {
            //TODO Custom implementation of LightningBolt?
            if(!(entity.getEntityMeta() instanceof LightningBoltMeta lightningBoltMeta))
                return false;

            return this.entityStruck == null || this.entityStruck.test(entity, null);
        }

        @Contract(pure = true)
        public LightningBolt requireBlocksSetOnFire(@Nullable Integer amount) {
            this.blocksSetOnFire = amount;
            return this;
        }

        @Contract(pure = true)
        public LightningBolt requireEntityStruckProperties(@Nullable PredicateEntityProps properties) {
            this.entityStruck = properties;
            return this;
        }
    }

    @Getter @Setter @AllArgsConstructor
    public static class FishingHook {
        @JsonValue private @NotNull Boolean inOpenWater; //Confusing documentation, is this correct?
    }
}
