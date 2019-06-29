package com.fleetmgr.sdk.client.state.device;

import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.client.state.State;
import com.fleetmgr.sdk.client.state.device.connected.Ready;
import com.fleetmgr.sdk.client.state.device.connected.Released;

import java.util.logging.Level;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class Connected extends State {

    private State internalState;

    Connected(State state) {
        super(state);
        this.internalState = new Ready(this);
    }

    @Override
    public State start() {
        internalState.start();
        backend.getHeartbeatHandler().start();
        listener.onEvent(new FacadeEvent(FacadeEvent.Type.ATTACHED));
        return null;
    }

    @Override
    public State notifyEvent(UserEvent event) {
        return onNewState(internalState.handleEvent(event));
    }

    @Override
    public State notifyConnection(ConnectionEvent event) {
        return onNewState(internalState.handleEvent(event));
    }

    private State onNewState(State newState) {
        while (newState != null) {
            logger.info("Connected transition: " + toString() + " -> Connected." + newState.toString());
            internalState = newState;
            newState = (State)internalState.start();
        }
        if (internalState instanceof Released) {
            return new Disconnecting(this);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Connected." + internalState;
    }
}
