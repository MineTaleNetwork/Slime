package cc.minetale.slime.utils.sequence;

import cc.minetale.slime.utils.sequence.SequenceBuilder.Executor;
import cc.minetale.slime.utils.sequence.SequenceBuilder.Repeater;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Sequence {

    public static final SchedulerManager SCHEDULER_MANAGER = MinecraftServer.getSchedulerManager();

    @Getter private final long countdownTime;
    @Getter private final long stepInterval;

    private final boolean experienceBar;

    private final SortedMap<Long, Executor> queuedExecutors;
    private final SortedMap<Long, List<Repeater>> queuedRepeaters;

    private final Consumer<List<?>> onFinish;

    @Getter private Task task;

    @Getter private long startTime;
    @Getter private long lastCheckedAt;

    @Getter private List<Object> involved = new ArrayList<>();
    private final List<Repeater> activeRepeaters = new ArrayList<>();

    public boolean start() {
        if(this.task != null) { return false; }

        var currentTime = System.currentTimeMillis();

        this.startTime = currentTime;
        this.lastCheckedAt = currentTime;

        var timeLeft = getTimeLeft(currentTime);

        //Find the first executor and execute it if it's an instant one
        if(!this.queuedExecutors.isEmpty()) {
            var executorTime = this.queuedExecutors.firstKey();
            var executor = this.queuedExecutors.get(executorTime);

            if(executorTime >= this.countdownTime) {
                try {
                    executor.execute(timeLeft, this.involved);
                } catch(Exception e) {
                    MinecraftServer.getExceptionManager().handleException(e);
                }
                this.queuedExecutors.remove(executorTime);
            }
        }

        //Find the first repeaters and start them if they're supposed to start instantly
        if(!this.queuedRepeaters.isEmpty()) {
            var repeatersTime = this.queuedRepeaters.firstKey();
            var repeaters = this.queuedRepeaters.get(repeatersTime);

            if(repeatersTime >= this.countdownTime) {
                for(Repeater repeater : repeaters) {
                    try {
                        repeater.execute(timeLeft, this.involved);
                    } catch(Exception e) {
                        MinecraftServer.getExceptionManager().handleException(e);
                    }
                    //Don't bother further checking the repeater if its next execution won't happen
                    //because the sequence will end by then or if the repeater has instantly ended
                    //(though in that case it could be replaced with an executor)
                    if(repeater.getNextExecutionTime() < 0) { continue; }
                    this.activeRepeaters.add(repeater);
                }
                this.queuedRepeaters.remove(repeatersTime);
            }
        }

        startTask();

        return true;
    }

    private void startTask() {
        this.task = SCHEDULER_MANAGER.buildTask(() -> {

            var currentTime = System.currentTimeMillis();
            var timeLeft = getTimeLeft(currentTime);

            System.out.println("Time Left: " + timeLeft);

            //Execute queued executors
            for(final var it = this.queuedExecutors.entrySet().iterator(); it.hasNext();) {
                var queued = it.next();
                var executorTime = queued.getKey();
                if(executorTime >= timeLeft) {
                    //Execute and remove the queued executor
                    var executor = queued.getValue();
                    try {
                        executor.execute(timeLeft, this.involved);
                    } catch(Exception e) {
                        MinecraftServer.getExceptionManager().handleException(e);
                    }
                    it.remove();
                } else {
                    break;
                }
            }

            //Execute and "activate" the queued repeaters first
            for(final var it = this.queuedRepeaters.entrySet().iterator(); it.hasNext();) {
                var queued = it.next();
                var repeaterTime = queued.getKey();
                if(repeaterTime >= timeLeft) {
                    //Execute the queued repeaters
                    var repeaters = queued.getValue();
                    for(Repeater repeater : repeaters) {
                        try {
                            repeater.execute(timeLeft, this.involved);
                        } catch(Exception e) {
                            MinecraftServer.getExceptionManager().handleException(e);
                        }
                        if(repeater.getNextExecutionTime() < 0) { continue; }
                        this.activeRepeaters.add(repeater);
                    }
                    it.remove();
                } else {
                    break;
                }
            }

            //And finally execute the active repeaters
            for(final var it = this.activeRepeaters.listIterator(); it.hasNext(); ) {
                var repeater = it.next();

                if(repeater.getNextExecutionTime() < 0 || repeater.getStopTime() >= timeLeft) {
                    it.remove();
                    continue;
                }

                if(repeater.getNextExecutionTime() >= timeLeft) {
                    System.out.println("A");
                    try {
                        repeater.execute(timeLeft, this.involved);
                    } catch(Exception e) {
                        MinecraftServer.getExceptionManager().handleException(e);
                    }
                }

                System.out.println(repeater.hashCode() + " Next Execution After: " + repeater.getNextExecutionTime());
            }

            if(timeLeft < 0) {
                this.task.cancel();
                this.onFinish.accept(this.involved);
            }

        }).repeat(this.stepInterval, ChronoUnit.MILLIS).schedule();
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
