package com.fleetmgr.sdk.client.state.pilot;

import java.util.logging.Level;
import com.fleetmgr.interfaces.ChannelResponse;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.client.state.pilot.connected.Recovering;
import com.fleetmgr.sdk.client.state.pilot.connected.Released;
import com.fleetmgr.sdk.client.state.pilot.connected.ValidatingChannels;
import com.fleetmgr.sdk.client.state.State;

import java.util.List;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class Connected extends State {

    private State internalState;

    Connected(State state, List<ChannelResponse> channels) {
        super(state);
        this.internalState = new ValidatingChannels(this, channels);
    }

    @Override
    public State start() {
        internalState.start();
        backend.getHeartbeatHandler().start();
        listener.onEvent(new FacadeEvent(FacadeEvent.Type.OPERATION_STARTED));
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
