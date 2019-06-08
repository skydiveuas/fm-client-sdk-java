package com.fleetmgr.sdk.client.state.device.connected;

import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.client.state.State;
import com.fleetmgr.interfaces.facade.control.ControlMessage;
import com.fleetmgr.sdk.system.capsule.Timer;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.11.2018
 * Description:
 */
public class Recovering extends State {

    private Timer recoveringTimer;

    Recovering(State state) {
        super(state);
    }

    @Override
    public State start() {
        backend.getHeartbeatHandler().end();
        recoveringTimer = client.executeAfter(() -> client.notifyEvent(
                new ConnectionEvent(ConnectionEvent.Type.CONNECTION_DROPPED)),
                backend.getSetupResponse().getRecoveringTimeoutMs());
        listener.onEvent(new FacadeEvent(FacadeEvent.Type.UNREACHABLE));
        backend.getChannelsHandler().closeAllChannels();
        return null;
    }

    @Override
    public State notifyEvent(UserEvent event) {
        switch (event.getType()) {
            case RELEASE:
                recoveringTimer.cancel();
                return new Released(this);

            default:
                return defaultEventHandle(event.toString());
        }
    }

    @Override
    public State notifyConnection(ConnectionEvent event) {
        switch (event.getType()) {
            case RECEIVED:
                return handleMessage(((Received)event).getMessage());

            case CONNECTION_DROPPED:
                return new Released(this);

            default:
                return defaultEventHandle(event.toString());
        }
    }

    private State handleMessage(ControlMessage message) {
        switch (message.getCommand()) {
            default:
                return defaultMessageHandle(message);
        }
    }

    @Override
    public String toString() {
        return "Recovering";
    }
}
