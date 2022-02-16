package cc.minetale.slime.loot.predicate.util;

import cc.minetale.slime.loot.util.DoubleRangeProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.Nullable;

@Getter @Setter @AllArgsConstructor
public class PredicatePosition {
    private @Nullable DoubleRangeProvider x;
    private @Nullable DoubleRangeProvider y;
    private @Nullable DoubleRangeProvider z;

    public boolean test(Point pos) {
        return (this.x == null || this.x.isInRange(pos.x())) &&
                (this.y == null || this.y.isInRange(pos.y())) &&
                (this.z == null || this.z.isInRange(pos.z()));
    }
}
