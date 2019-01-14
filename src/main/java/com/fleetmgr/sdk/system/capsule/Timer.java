package com.fleetmgr.sdk.system.capsule;

import java.util.TimerTask;

/**
 * Created by: Bartosz Nawrot
 * Date: 21.10.2018
 * Description:
 */
public class Timer {

    public interface Listener {
        void onCanceled(Timer timer);
    }

    private java.util.Timer timer;

    private final Long id;
    private final Listener listener;

    private boolean canceled = false;
    private boolean recurring = false;

    Timer(Runnable task,
          long delay,
          long interval,
          Long id,
          Listener listener) {

        this.id = id;
        this.listener = listener;
        this.recurring = true;

        this.timer = new java.util.Timer();
        this.timer.schedule(createTimerTask(task), delay, interval);
    }

    Timer(Runnable task,
          long timeout,
          Long id,
          Listener listener) {

        this.id = id;
        this.listener = listener;

        this.timer = new java.util.Timer();
        this.timer.schedule(createTimerTask(task), timeout, Long.MAX_VALUE);
    }

    public void cancel() {
        if (!canceled) {
            canceled = true;
            timer.cancel();
            listener.onCanceled(this);
        }
    }

    public Long getId() {
        return id;
    }

    private TimerTask createTimerTask(Runnable task) {
        return new TimerTask() {
            @Override
            public void run() {
                task.run();
                if (!recurring) {
                    timer.cancel();
                    canceled = true;
                }
            }
        };
    }
}
