package com.fleetmgr.sdk.system.machine;

import com.fleetmgr.sdk.system.capsule.Capsule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by: Bartosz Nawrot
 * Date: 21.10.2018
 * Description:
 */
public abstract class StateMachine<Event> extends Capsule {

    private static final Logger logger = LoggerFactory.getLogger(StateMachine.class);

    private State<Event> state;

    private Deque<Event> deferred;

    public StateMachine(ExecutorService executor, State<Event> initial) {
        super(executor);
        this.state = initial;
        this.deferred = new LinkedBlockingDeque<>();
    }

    public void notifyEvent(Event event) {
        execute(() -> {
            logger.debug("{}: Handling: {}", this, event);
            State<Event> newState = state.handleEvent(event);
            while (newState != null) {
                logger.info("{}: Transition to: {}", this, newState);
                state = newState;
                newState = state.start();
            }
        });
    }

    protected void setState(State<Event> newState) {
        logger.info("{}: Forced transition to: {}", this, newState);
        state = newState;
        state.start();
    }

    public void defer(Event event) {
        logger.debug("{}: Deferring: {}", this, event);
        deferred.add(event);
    }

    public void recall() {
        if (!deferred.isEmpty()) {
            Event event = deferred.poll();
            logger.debug("{}: Recalling: {}, remaining queue: {}", this, event, deferred);
            notifyEvent(event);
        }
    }

    public String getStateName() {
        if (state != null) {
            return state.toString();
        } else {
            return "uninitialized";
        }
    }
}
