package cc.minetale.slime.utils.sequence;

import cc.minetale.slime.utils.sequence.SequenceBuilder.Executor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Sequence {

    public static final SchedulerManager SCHEDULER_MANAGER = MinecraftServer.getSchedulerManager();

    @Getter private final long countdownTime;
    private final boolean experienceBar;

    @Getter private long currentInterval;

    @Getter private long currentExecutorTime;

    private NavigableMap<Long, Executor> queuedExecutors;

    private final Consumer<List<?>> onFinish;

    @Getter private Task task;

    @Getter private long startTime;

    @Getter private List<Object> involved = new ArrayList<>();


    public Sequence(long countdownTime, boolean experienceBar, NavigableMap<Long, Executor> queuedExecutors, Consumer<List<?>> onFinish) {
        this.countdownTime = countdownTime;
        this.experienceBar = experienceBar;
        this.queuedExecutors = queuedExecutors;
        this.onFinish = onFinish;
    }

    public boolean start() {
        if(this.task != null) { return false; }

        this.startTime = System.currentTimeMillis();

        //Find the first executor and execute it if it's an instant one
        if(!this.queuedExecutors.isEmpty()) {
            var executorTime = this.queuedExecutors.firstKey();
            var executor = this.queuedExecutors.get(executorTime);

            this.currentInterval = this.countdownTime - executorTime;

            if(executorTime >= this.countdownTime) {
                try {
                    executor.execute(this.countdownTime, this.involved);
                } catch(Exception e) {
                    MinecraftServer.getExceptionManager().handleException(e);
                }
                this.queuedExecutors.remove(executorTime);

                this.currentExecutorTime = this.queuedExecutors.firstKey();
                this.currentInterval = this.countdownTime - this.currentExecutorTime;
            }
        }

        startTask();

        return true;
    }

    private void startTask() {
        this.task = SCHEDULER_MANAGER.buildTask(() -> {

            //Execute queued executor
            if(!this.queuedExecutors.isEmpty()) {
                var executorTime = this.queuedExecutors.firstKey();
                var executor = this.queuedExecutors.get(executorTime);
                try {
                    executor.execute(executorTime, this.involved);
                } catch(Exception e) {
                    MinecraftServer.getExceptionManager().handleException(e);
                }
                this.queuedExecutors.remove(executorTime);

                if(!this.queuedExecutors.isEmpty()) {
                    this.currentExecutorTime = this.queuedExecutors.firstKey();
                } else {
                    this.currentExecutorTime = 0;
                }
                this.currentInterval = executorTime - this.currentExecutorTime;
            } else {
                this.onFinish.accept(this.involved);
                return;
            }

            startTask();

        }).delay(this.currentInterval, ChronoUnit.MILLIS).schedule();
    }

    public boolean addInvolved(Object obj) {
        return this.involved.add(obj);
    }

    public long getFinishTime() {
        return this.startTime + this.countdownTime;
    }

    public long getTimeLeft(long currentTime) {
        return getFinishTime() - currentTime;
    }

    public long getTimeLeft() {
        return getTimeLeft(System.currentTimeMillis());
    }

}
