package cc.minetale.slime.utils.sequence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.title.Title;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.MathUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SequenceBuilder {

    @Getter private long totalTime;
    @Getter private long stepInterval = 200;

    @Getter private boolean experienceBar = false;

    private final SortedMap<Long, Executor> executors = new TreeMap<>(Collections.reverseOrder());
    private final SortedMap<Long, List<Repeater>> repeaters = new TreeMap<>(Collections.reverseOrder());

    private Consumer<List<?>> onFinish;

    public SequenceBuilder(long totalTime) {
        this.totalTime = totalTime;
    }

    public SequenceBuilder setTotalTime(long totalTime) {
        this.totalTime = totalTime;
        return this;
    }

    public SequenceBuilder setStepInterval(long stepInterval) {
        this.stepInterval = stepInterval;
        return this;
    }

    public SequenceBuilder setExperienceBar(boolean experienceBar) {
        this.experienceBar = experienceBar;
        return this;
    }

    public SequenceBuilder chat(long timeLeft, Component component) {
        execute(timeLeft, (currentTimeLeft, involved) -> chat(component, currentTimeLeft, involved));
        return this;
    }

    public SequenceBuilder chatRepeat(long interval, Component component) {
        repeat(interval, (currentTimeLeft, involved) -> chat(component, currentTimeLeft, involved));
        return this;
    }

    public SequenceBuilder chatRepeat(long timeLeft, long interval, Component component) {
        repeat(timeLeft, interval, (currentTimeLeft, involved) -> chat(component, currentTimeLeft, involved));
        return this;
    }

    public SequenceBuilder chatRepeat(long timeLeft, long stopTime, long interval, Component component) {
        repeat(timeLeft, stopTime, interval, (currentTimeLeft, involved) -> chat(component, currentTimeLeft, involved));
        return this;
    }

    private void chat(Component component, long timeLeft, List<?> involved) {
        for(Object obj : involved) {
            if(!(obj instanceof Player)) { continue; }
            var player = (Player) obj;
            var config = TextReplacementConfig.builder()
                    .matchLiteral("%d")
                    .replacement(String.valueOf(timeLeft / (double)1000))
                    .build();

            var newComponent = component.replaceText(config);

            player.sendMessage(newComponent);
        }
    }

    public SequenceBuilder title(long timeLeft, Title title) {
        execute(timeLeft, (currentTimeLeft, involved) -> title(title, currentTimeLeft, involved));
        return this;
    }

    public SequenceBuilder titleRepeat(long interval, Title title) {
        execute(interval, (currentTimeLeft, involved) -> title(title, currentTimeLeft, involved));
        return this;
    }

    public SequenceBuilder titleRepeat(long timeLeft, long interval, Title title) {
        repeat(timeLeft, interval, (currentTimeLeft, involved) -> title(title, currentTimeLeft, involved));
        return this;
    }

    public SequenceBuilder titleRepeat(long timeLeft, long stopTime, long interval, Title title) {
        repeat(timeLeft, stopTime, interval, (currentTimeLeft, involved) -> title(title, currentTimeLeft, involved));
        return this;
    }

    private void title(Title title, long timeLeft, List<?> involved) {
        for(Object obj : involved) {
            if(!(obj instanceof Player)) { continue; }
            var player = (Player) obj;
            var config = TextReplacementConfig.builder()
                    .matchLiteral("%d")
                    .replacement(String.valueOf(timeLeft))
                    .build();

            var newTitle = title.title().replaceText(config);
            var newSubtitle = title.subtitle().replaceText(config);

            player.showTitle(Title.title(newTitle, newSubtitle, title.times()));
        }
    }

    public SequenceBuilder actionBar(long timeLeft, TextComponent component) {
        execute(timeLeft, (currentTimeLeft, involved) -> actionBar(component, currentTimeLeft, involved));
        return this;
    }

    public SequenceBuilder actionBarRepeat(long interval, TextComponent component) {
        repeat(interval, (currentTimeLeft, involved) -> actionBar(component, currentTimeLeft, involved));
        return this;
    }

    public SequenceBuilder actionBarRepeat(long timeLeft, long interval, TextComponent component) {
        repeat(timeLeft, interval, (currentTimeLeft, involved) -> actionBar(component, currentTimeLeft, involved));
        return this;
    }

    public SequenceBuilder actionBarRepeat(long timeLeft, long stopTime, long interval, TextComponent component) {
        repeat(timeLeft, stopTime, interval, (currentTimeLeft, involved) -> actionBar(component, currentTimeLeft, involved));
        return this;
    }

    private void actionBar(TextComponent component, long timeLeft, List<?> involved) {
        for(Object obj : involved) {
            if(!(obj instanceof Player)) { continue; }
            var player = (Player) obj;
            var config = TextReplacementConfig.builder()
                    .matchLiteral("%d")
                    .replacement(String.valueOf(timeLeft))
                    .build();

            player.sendActionBar(component.replaceText(config));
        }
    }

    public SequenceBuilder onFinish(Consumer<List<?>> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    public SequenceBuilder execute(long timeLeft, BiConsumer<Long, List<?>> consumer) {
        var otherExecutor = this.executors.get(timeLeft);
        if(otherExecutor != null) {
            var otherConsumer = otherExecutor.getConsumer();
            consumer = otherConsumer.andThen(consumer);
        }

        this.executors.put(timeLeft, new Executor(consumer));
        return this;
    }

    public SequenceBuilder repeat(long interval, BiConsumer<Long, List<?>> consumer) {
        addRepeat(Long.MAX_VALUE, Long.MIN_VALUE, interval, consumer);
        return this;
    }

    public SequenceBuilder repeat(long timeLeft, long interval, BiConsumer<Long, List<?>> consumer) {
        addRepeat(timeLeft, Long.MIN_VALUE, interval, consumer);
        return this;
    }

    public SequenceBuilder repeat(long timeLeft, long stopTime, long interval, BiConsumer<Long, List<?>> consumer) {
        addRepeat(timeLeft, stopTime, interval, consumer);
        return this;
    }

    private void addRepeat(long timeLeft, long stopTime, long interval, BiConsumer<Long, List<?>> consumer) {
        this.repeaters.compute(timeLeft, (key, value) -> {
            if(value == null)
                value = new ArrayList<>();

            value.add(new Repeater(interval, stopTime, consumer));
            return value;
        });
    }

    public Sequence build() {
        if(this.experienceBar) {
            repeat(0, (timeLeft, involved) -> {
                involved.forEach(obj -> {
                    if(!(obj instanceof Player)) { return; }
                    Player player = (Player) obj;
                    player.setExp(MathUtils.clamp(timeLeft / (float)this.totalTime, 0, 1));
                });
            });
        }

        return new Sequence(
                this.totalTime,
                this.stepInterval,

                this.experienceBar,

                this.executors,
                this.repeaters,

                this.onFinish);
    }

    @Getter @AllArgsConstructor
    static class Executor {
        private BiConsumer<Long, List<?>> consumer;

        public void execute(long timeLeft, List<?> involved) {
            this.consumer.accept(timeLeft, involved);
        }
    }

    @Getter
    static class Repeater {
        private long interval; //Interval between executions
        private long stopTime; //Timeleft of the cooldown when this should stop repeating

        @Setter private long lastExecution = Long.MAX_VALUE;

        private BiConsumer<Long, List<?>> consumer;

        public Repeater(long interval, long stopTime, BiConsumer<Long, List<?>> consumer) {
            this.interval = interval;
            this.stopTime = stopTime;

            this.consumer = consumer;
        }

        public void execute(long timeLeft, List<?> involved) {
            this.consumer.accept(timeLeft, involved);
            this.lastExecution = timeLeft;
        }

        public long getNextExecutionTime() {
            return this.lastExecution - this.interval;
        }
    }

}
