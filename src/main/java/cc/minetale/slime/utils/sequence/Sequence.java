package cc.minetale.slime.utils.sequence;

import cc.minetale.slime.utils.sequence.SequenceBuilder.Executor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static cc.minetale.slime.Slime.SCHEDULER_MANAGER;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Sequence {

    @Getter private final long totalTime;

    @Getter private long currentInterval;
    @Getter private long currentExecutorTime;

    private NavigableMap<Long, Executor> executors;

    private final Consumer<List<?>> onFinish;
    private final BiConsumer<Long, List<?>> onCancel;

    @Getter private Task task;

    @Getter private long startedAt;
    @Getter private long lastTaskScheduledAt;

    @Getter private boolean isPaused;
    @Getter private long pausedAt;

    @Getter private List<Object> involved = new ArrayList<>();

    public Sequence(long totalTime, NavigableMap<Long, Executor> executors, Consumer<List<?>> onFinish, BiConsumer<Long, List<?>> onCancel) {
        this.totalTime = totalTime;
        this.executors = executors;
        this.onFinish = onFinish;
        this.onCancel = onCancel;
    }

    public boolean start() {
        if(this.task != null) { return false; }

        this.startedAt = System.currentTimeMillis();

        //Find the first executor and execute it if it's an instant one
        if(!this.executors.isEmpty()) {
            var executorTime = this.executors.firstKey();
            var executor = this.executors.get(executorTime);

            this.currentInterval = this.totalTime - executorTime;

            if(executorTime >= this.totalTime) {
                try {
                    executor.execute(this.totalTime, this.involved);
                } catch(Exception e) {
                    MinecraftServer.getExceptionManager().handleException(e);
                }
                this.executors.remove(executorTime);

                this.currentExecutorTime = this.executors.firstKey();
                this.currentInterval = this.totalTime - this.currentExecutorTime;
            }
        }

        startTask();

        return true;
    }

    public boolean pause() {
        if(this.isPaused) { return false; }

        this.task.cancel();

        this.isPaused = true;
        this.pausedAt = this.executors.higherKey(this.currentExecutorTime) + (System.currentTimeMillis() - this.lastTaskScheduledAt);

        return true;
    }

    public boolean resume() {
        if(!this.isPaused) { return false; }

        this.task.cancel();

        this.isPaused = false;
        this.currentInterval = this.currentExecutorTime - this.pausedAt;

        startTask();

        return true;
    }

    public boolean cancel() {
        if(this.task == null) { return false; }

        this.task.cancel();

        long timeLeft = this.executors.higherKey(this.currentExecutorTime) + (System.currentTimeMillis() - this.lastTaskScheduledAt);
        this.onCancel.accept(timeLeft, this.involved);

        return true;
    }

    private void startTask() {
        var builder = SCHEDULER_MANAGER.buildTask(() -> {
            //Execute queued executor
            if(!this.executors.isEmpty()) {
                var executorTime = this.executors.firstKey();
                var executor = this.executors.get(executorTime);
                try {
                    executor.execute(executorTime, this.involved);
                } catch(Exception e) {
                    MinecraftServer.getExceptionManager().handleException(e);
                }
                this.executors.remove(executorTime);

                if(!this.executors.isEmpty()) {
                    this.currentExecutorTime = this.executors.firstKey();
                } else {
                    this.currentExecutorTime = 0;
                }
                this.currentInterval = executorTime - this.currentExecutorTime;
            } else {
                this.onFinish.accept(this.involved);
                return;
            }

            startTask();

        }).delay(this.currentInterval, ChronoUnit.MILLIS);

        this.lastTaskScheduledAt = System.currentTimeMillis();

        this.task = builder.schedule();
    }

    public boolean addInvolved(Object obj) {
        return this.involved.add(obj);
    }

    public boolean addInvolved(Collection<?> objs) {
        return this.involved.addAll(objs);
    }

    public long getFinishTime() {
        return this.startedAt + this.totalTime;
    }

    public long getTimeLeft(long currentTime) {
        return getFinishTime() - currentTime;
    }

    public long getTimeLeft() {
        return getTimeLeft(System.currentTimeMillis());
    }

}
