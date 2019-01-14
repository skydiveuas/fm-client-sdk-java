package com.fleetmgr.sdk.system.capsule;

import com.fleetmgr.sdk.system.Pair;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by: Bartosz Nawrot
 * Date: 21.10.2018
 * Description:
 */
public class Capsule {

    private final Long NOT_TIMER_ID = 0L;

    private ExecutorService executor;

    private ReentrantLock lock;

    private Queue<Pair<Runnable, Long>> queue;
    private AtomicBoolean processing;

    private Long lastTimerId;

    public Capsule(ExecutorService executor) {
        this.executor = executor;

        this.lock = new ReentrantLock();

        this.queue = new LinkedList<>();
        this.processing = new AtomicBoolean(false);

        this.lastTimerId = NOT_TIMER_ID;
    }

    public void execute(Runnable task) {
        lock.lock();
        if (processing.get()) {
            queue.add(new Pair<>(() -> {
                task.run();
                proceed();
            }, NOT_TIMER_ID));
        }
        else {
            processing.set(true);
            executor.execute(() -> {
                task.run();
                proceed();
            });
        }
        lock.unlock();
    }

    public Timer executeAfter(Runnable task, long timeout) {
        lastTimerId++;
        if (lastTimerId.equals(NOT_TIMER_ID)) lastTimerId++;
        return new Timer(task, timeout, lastTimerId, this::cancel);
    }

    public Timer executeEvery(Runnable task, long delay, long interval) {
        lastTimerId++;
        if (lastTimerId.equals(NOT_TIMER_ID)) lastTimerId++;
        return new Timer(task, delay, interval, lastTimerId, this::cancel);
    }

    private void cancel(Timer timer) {
        lock.lock();
        for (Pair<Runnable, Long> task : queue) {
            if (task.getValue().equals(timer.getId())) {
                queue.remove(task);
            }
        }
        lock.unlock();
    }

    private void proceed() {
        lock.lock();
        Pair<Runnable, Long> polled = queue.poll();
        if (polled != null) {
            executor.execute(polled.getKey());
        }
        else {
            processing.set(false);
        }
        lock.unlock();
    }
}
