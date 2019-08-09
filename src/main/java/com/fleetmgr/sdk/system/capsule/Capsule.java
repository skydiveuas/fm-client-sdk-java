package com.fleetmgr.sdk.system.capsule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private ExecutorService executor;

    private ReentrantLock lock;

    private Queue<Runnable> queue;
    private AtomicBoolean processing;

    public Capsule(ExecutorService executor) {
        this.executor = executor;

        this.lock = new ReentrantLock();

        this.queue = new LinkedList<>();
        this.processing = new AtomicBoolean(false);
    }

    public void execute(Runnable task) {
        lock.lock();
        if (processing.get()) {
            queue.add(() -> {
                task.run();
                proceed();
            });
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
        return new Timer(task, timeout);
    }

    public Timer executeEvery(Runnable task, long delay, long interval) {
        return new Timer(task, delay, interval);
    }

    private void proceed() {
        lock.lock();
        Runnable polled = queue.poll();
        if (polled != null) {
            executor.execute(polled);
        }
        else {
            processing.set(false);
        }
        lock.unlock();
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
