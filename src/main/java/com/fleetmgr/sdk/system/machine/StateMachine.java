package com.fleetmgr.sdk.system.machine;

import com.fleetmgr.sdk.system.capsule.Capsule;
import org.slf4j.Logger;

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
            getLogger().info("{}: Handling: {}", state, event);
            State<Event> newState = state.handleEvent(event);
            while (newState != null) {
                getLogger().info("{}: Transition to: {}",
                        state, newState);
                state = newState;
                newState = state.start();
            }
        });
    }

    protected void setState(State<Event> state) {
        getLogger().info("{}: Forced transition to: {}",
                this.state, state);
        this.state = state;
        this.state.start();
    }

    public void defer(Event event) {
        getLogger().debug("{}: Deferring: {}", state, event );
        deferred.add(event);
    }

    public void recall() {
        if (!deferred.isEmpty()) {
            Event event = deferred.poll();
            getLogger().debug("{}: Recalling: {}, remaining queue: {}",
                    state, event, deferred);
            notifyEvent(event);
        }
    }

    public String getStateName() {
        return state.toString();
    }

    protected abstract Logger getLogger();
}
