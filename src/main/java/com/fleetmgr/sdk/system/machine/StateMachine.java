package com.fleetmgr.sdk.system.machine;

import com.fleetmgr.sdk.system.capsule.Capsule;
import java.util.logging.Level;

import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by: Bartosz Nawrot
 * Date: 21.10.2018
 * Description:
 */
public abstract class StateMachine<Event> extends Capsule {

    private State<Event> state;

    private Deque<Event> deferred;

    public StateMachine(ExecutorService executor, State<Event> initial) {
        super(executor);
        this.state = initial;
        this.deferred = new LinkedBlockingDeque<>();
    }

    public void notifyEvent(Event event) {
        execute(() -> {
            log(Level.INFO,"Handling: " + event + " @ " + state);
            State<Event> newState = state.handleEvent(event);
            while (newState != null) {
                log(Level.INFO,"Transition: " + state + " -> " + newState);
                state = newState;
                newState = state.start();
            }
        });
    }

    protected void setState(State<Event> state) {
        this.state = state;
        this.state.start();
    }

    public void defer(Event event) {
        log(Level.FINE,"Deferring: " + event +  " @ " + state);
        deferred.add(event);
    }

    public void recall() {
        if (!deferred.isEmpty()) {
            Event event = deferred.poll();
            log(Level.FINE, "Recalling: " + event +  " @ " + state + ", remaining queue: " + deferred);
            notifyEvent(event);
        }
    }

    public String getStateName() {
        return state.toString();
    }

    protected abstract void log(Level level, String message);
}
