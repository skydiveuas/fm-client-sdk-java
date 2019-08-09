package com.fleetmgr.sdk.system.capsule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * Created by: Bartosz Nawrot
 * Date: 21.10.2018
 * Description:
 */
public class Timer {

    private java.util.Timer timer;

    private boolean canceled = false;
    private boolean recurring = false;

    Timer(Runnable task,
          long delay,
          long interval) {
        this.recurring = true;
        this.timer = new java.util.Timer();
        this.timer.schedule(createTimerTask(task), delay, interval);
    }

    Timer(Runnable task,
          long timeout) {
        this.timer = new java.util.Timer();
        this.timer.schedule(createTimerTask(task), timeout, Long.MAX_VALUE);
    }

    public void cancel() {
        if (!canceled) {
            canceled = true;
            timer.cancel();
        }
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
