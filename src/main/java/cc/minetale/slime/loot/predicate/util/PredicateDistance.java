package cc.minetale.slime.loot.predicate.util;

import cc.minetale.slime.loot.util.FloatRangeProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@Getter @NoArgsConstructor
public class PredicateDistance {
    private @Nullable FloatRangeProvider absolute;
    private @Nullable FloatRangeProvider horizontal;
    private @Nullable FloatRangeProvider x;
    private @Nullable FloatRangeProvider y;
    private @Nullable FloatRangeProvider z;

    //TODO Factory methods

    public boolean test(Point first, Point second) {
        final var xDist = Math.abs(first.x() - second.x());
        if(this.x != null && !this.x.isInRange(xDist))
            return false;

        final var yDist = Math.abs(first.y() - second.y());
        if(this.y != null && !this.y.isInRange(yDist))
            return false;

        final var zDist = Math.abs(first.z() - second.z());
        if(this.z != null && !this.z.isInRange(zDist))
            return false;

        final var absoluteDist = first.distance(second);
        if(absolute != null && !this.absolute.isInRange(absoluteDist))
            return false;

        final var horizontalDist = first.withY(0).distance(second.withY(0));
        return horizontal == null || this.horizontal.isInRange(horizontalDist);
    }

    @Contract(pure = true)
    public PredicateDistance requireAbsolute(@Nullable FloatRangeProvider range) {
        this.absolute = range;
        return this;
    }

    @Contract(pure = true)
    public PredicateDistance requireHorizontal(@Nullable FloatRangeProvider range) {
        this.horizontal = range;
        return this;
    }

    @Contract(pure = true)
    public PredicateDistance requireX(@Nullable FloatRangeProvider range) {
        this.x = range;
        return this;
    }

    @Contract(pure = true)
    public PredicateDistance requireY(@Nullable FloatRangeProvider range) {
        this.y = range;
        return this;
    }

    @Contract(pure = true)
    public PredicateDistance requireZ(@Nullable FloatRangeProvider range) {
        this.z = range;
        return this;
    }
}
