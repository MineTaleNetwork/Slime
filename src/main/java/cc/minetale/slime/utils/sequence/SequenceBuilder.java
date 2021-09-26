package cc.minetale.slime.utils.sequence;

import cc.minetale.mlib.util.MathUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.title.Title;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.MathUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SequenceBuilder {

    @Getter private long totalTime;

    private boolean expBar = false;
    private long expBarInterval = 200;

    private final NavigableMap<Long, Executor> executors = new TreeMap<>(Collections.reverseOrder());

    private Consumer<List<?>> onFinish;
    private BiConsumer<Long, List<?>> onCancel;

    public SequenceBuilder(long totalTime) {
        this.totalTime = totalTime;
    }

    public SequenceBuilder setTotalTime(long totalTime) {
        this.totalTime = totalTime;
        return this;
    }

    public SequenceBuilder animateExperienceBar(boolean isAnimated, long updateInterval) {
        this.expBar = isAnimated;
        this.expBarInterval = updateInterval;
        return this;
    }

    public SequenceBuilder animateExperienceBar(boolean isAnimated) {
        this.expBar = isAnimated;
        return this;
    }

    public boolean isExperienceBarAnimated() {
        return this.expBar;
    }

    public long getExperienceBarInterval() {
        return this.expBarInterval;
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
                    .replacement(String.valueOf((int) Math.ceil(timeLeft / (double) 1000)))
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
                    .replacement(String.valueOf((int) Math.ceil(timeLeft / (double) 1000)))
                    .build();

            var newTitle = title.title().replaceText(config);
            var newSubtitle = title.subtitle().replaceText(config);

            player.showTitle(Title.title(Component.empty(), Component.empty()));
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
                    .replacement(String.valueOf((int) Math.ceil(timeLeft / (double) 1000)))
                    .build();

            player.sendActionBar(component.replaceText(config));
        }
    }

    public SequenceBuilder sound(long timeLeft, Sound sound, @Nullable Sound.Emitter emitter, @Nullable Vec pos) {
        execute(timeLeft, (currentTimeLeft, involved) -> sound(sound, emitter, pos, involved));
        return this;
    }

    public SequenceBuilder soundRepeat(long interval, Sound sound, @Nullable Sound.Emitter emitter, @Nullable Vec pos) {
        repeat(interval, (currentTimeLeft, involved) -> sound(sound, emitter, pos, involved));
        return this;
    }

    public SequenceBuilder soundRepeat(long timeLeft, long interval,
                                       Sound sound, @Nullable Sound.Emitter emitter, @Nullable Vec pos) {

        repeat(timeLeft, interval, (currentTimeLeft, involved) -> sound(sound, emitter, pos, involved));
        return this;
    }

    public SequenceBuilder soundRepeat(long timeLeft, long stopTime, long interval,
                                       Sound sound, @Nullable Sound.Emitter emitter, @Nullable Vec pos) {

        repeat(timeLeft, stopTime, interval, (currentTimeLeft, involved) -> sound(sound, emitter, pos, involved));
        return this;
    }

    private void sound(Sound sound, @Nullable Sound.Emitter emitter, @Nullable Vec pos, List<?> involved) {
        for(Object obj : involved) {
            if(!(obj instanceof Player)) { continue; }
            var player = (Player) obj;

            if(emitter != null) {
                player.playSound(sound, emitter);
            } else if(pos != null) {
                player.playSound(sound, pos.x(), pos.y(), pos.z());
            } else {
                player.playSound(sound);
            }
        }
    }

    public SequenceBuilder onFinish(Consumer<List<?>> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    public SequenceBuilder onCancel(BiConsumer<Long, List<?>> onCancel) {
        this.onCancel = onCancel;
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
        timeLeft = MathUtil.clamp(timeLeft, 0, this.totalTime);
        stopTime = MathUtil.clamp(stopTime, 0, timeLeft);
        interval = MathUtil.clamp(interval, 50, this.totalTime);
        for(long executorTime = timeLeft; executorTime >= stopTime; executorTime -= interval) {
            execute(executorTime, consumer);
        }
    }

    public Sequence build() {
        if(this.expBar) {
            repeat(this.expBarInterval, (timeLeft, involved) -> {
                involved.forEach(obj -> {
                    if(!(obj instanceof Player)) { return; }
                    Player player = (Player) obj;
                    player.setExp(MathUtils.clamp(timeLeft / (float)this.totalTime, 0, 1));
                });
            });
        }

        return new Sequence(
                this.totalTime,

                this.executors,

                this.onFinish,
                this.onCancel);
    }

    @Getter @AllArgsConstructor
    static class Executor {
        private BiConsumer<Long, List<?>> consumer;

        public void execute(long timeLeft, List<?> involved) {
            this.consumer.accept(timeLeft, involved);
        }
    }

}
