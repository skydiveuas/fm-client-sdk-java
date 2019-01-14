package com.fleetmgr.sdk.client.state.device;

import java.util.logging.Level;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.state.device.connected.Ready;
import com.fleetmgr.sdk.client.state.device.connected.Recovering;
import com.fleetmgr.sdk.client.state.device.connected.Released;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.client.state.State;

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
        boolean wasRecovering = internalState instanceof Recovering;
        while (newState != null) {
            log(Level.INFO, "Connected transition: " + toString() + " -> Connected." + newState.toString());
            internalState = newState;
            newState = (State)internalState.start();
        }
        if (internalState instanceof Released) {
            return new Disconnecting(this, wasRecovering);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Connected." + internalState;
    }
}
