package cc.minetale.slime.loot.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Getter
public abstract class NumberProvider {
    public static final NumberProvider ZERO = new NumberProvider(NumberProviderType.CONSTANT) {
        @Override public float get() { return 0; }
    };

    @Nullable NumberProviderType type;

    protected NumberProvider(NumberProviderType type) {
        this.type = type;
    }

    public abstract float get();

    public static class Constant extends NumberProvider {
        private float value;

        @JsonCreator
        public Constant(float value) {
            super(NumberProviderType.CONSTANT);
            this.value = value;
        }

        public Constant(float value, boolean unwrapped) {
            super(!unwrapped ? null : NumberProviderType.CONSTANT);
            this.value = value;
        }

        @Override
        public float get() {
            return this.value;
        }

        public void set(float value) {
            this.value = value;
        }
    }

    @Getter @Setter
    public static class Uniform extends NumberProvider {
        private float min;
        private float max;

        public Uniform(float min, float max) {
            super(NumberProviderType.UNIFORM);
            this.min = min;
            this.max = max;
        }

        @Override
        public float get(){
            final var lowerBound = this.min;
            final var upperBound = this.max - lowerBound;
            return lowerBound + (ThreadLocalRandom.current().nextFloat() * upperBound);
        }
    }

    @Getter @Setter
    public static class Binomial extends NumberProvider {
        @JsonProperty("n") private int trials;
        @JsonProperty("p") private float probability;

        public Binomial(int trials, float probability) {
            super(NumberProviderType.BINOMIAL);
            this.trials = trials;
            this.probability = probability;
        }

        @Override
        public float get(){
            final var rng = ThreadLocalRandom.current();
            return IntStream.range(0, this.trials)
                    .filter(trial -> rng.nextDouble() < this.probability)
                    .sum();
        }
    }

    //TODO Score
}
