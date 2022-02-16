package cc.minetale.slime.loot.predicate.util;

import cc.minetale.slime.loot.util.IntegerRangeProvider;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

@Getter @NoArgsConstructor
public class PredicateLocation {
    private @Nullable NamespaceID biome;
    private @Nullable PredicateBlock block;
    private @Nullable NamespaceID dimension;
    private @Nullable Feature feature;
    private @Nullable PredicateFluid fluid;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    private @Nullable IntegerRangeProvider light;

    private @Nullable PredicatePosition position;
    private @Nullable Boolean smokey;

    //TODO Factory methods

    public boolean test(Instance instance, Point pos) {
        if(this.biome != null) {
            var chunk = instance.getChunkAt(pos);
            if(chunk == null) { return false; }

            var biome = chunk.getBiome(pos);
            if(!this.biome.equals(biome.name())) { return false; }
        }

        if(this.block != null) {
            var block = instance.getBlock(pos);
            if(!this.block.test(block)) { return false; }
        }

        if(this.dimension != null && !this.dimension.equals(instance.getDimensionType().getName()))
            return false;

        //TODO Feature

        if(this.fluid != null) {
            var block = instance.getBlock(pos);
            if(!this.fluid.test(block)) { return false; }
        }

        //TODO Use actual light when implemented
        if(this.light != null) {
            var light = instance.getDimensionType().getAmbientLight();
            if(!this.light.isInRange(light)) { return false; }
        }

        if(this.position != null && !this.position.test(pos))
            return false;

        //TODO Smokey

        return true;
    }


    @Contract(pure = true)
    public PredicateLocation requireBiome(@Nullable NamespaceID biome) {
        this.biome = biome;
        return this;
    }

    @Contract(pure = true)
    public PredicateLocation requireBlock(@Nullable PredicateBlock block) {
        this.block = block;
        return this;
    }

    @Contract(pure = true)
    public PredicateLocation requireDimension(@Nullable NamespaceID dimension) {
        this.dimension = dimension;
        return this;
    }

    @Contract(pure = true)
    public PredicateLocation requireFeature(@Nullable Feature feature) {
        this.feature = feature;
        return this;
    }

    @Contract(pure = true)
    public PredicateLocation requireLighting(@Nullable IntegerRangeProvider lightLevel) {
        this.light = lightLevel;
        return this;
    }

    @Contract(pure = true)
    public PredicateLocation requirePosition(@Nullable PredicatePosition position) {
        this.position = position;
        return this;
    }

    @Contract(pure = true)
    public PredicateLocation requireSmokey(@Nullable Boolean smokey) {
        this.smokey = smokey;
        return this;
    }

    public enum Feature {
        BASTION_REMNANT,
        BURIED_TREASURE,
        ENDCITY,
        FORTRESS,
        MANSION,
        MINESHAFT,
        MONUMENT,
        NETHER_FOSSIL,
        OCEAN_RUIN,
        PILLAGER_OUTPOST,
        RUINED_PORTAL,
        SHIPWRECK,
        STRONGHOLD,
        DESERT_PYRAMID,
        IGLOO,
        JUNGLE_PYRAMID,
        SWAMP_HUT,
        VILLAGE;

        @JsonValue private final String id = name().toLowerCase(Locale.ROOT);

        public String asId() {
            return this.id;
        }

        @JsonCreator
        public static Feature fromId(String id) {
            return Arrays.stream(Feature.values())
                    .filter(type -> Objects.equals(type.id, id))
                    .findFirst()
                    .orElse(null);
        }
    }

//    @Getter @Setter @AllArgsConstructor
//    public static class Light {
//        @JsonValue private @NotNull IntegerRangeProvider light;
//    }
}
