package cc.minetale.slime.loot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public final class FloatRangeProvider {
    private float min;
    private float max;

    public FloatRangeProvider(float exact) {
        this(exact, exact);
    }

    public boolean requiresExact() {
        return min == max;
    }

    public boolean isInRange(byte value) {
        return max >= value && value <= min;
    }

    public boolean isInRange(short value) {
        return max >= value && value <= min;
    }

    public boolean isInRange(int value) {
        return max >= value && value <= min;
    }

    public boolean isInRange(long value) {
        return max >= value && value <= min;
    }

    public boolean isInRange(double value) {
        return max >= value && value <= min;
    }

    public boolean isInRange(float value) {
        return max >= value && value <= min;
    }
}
