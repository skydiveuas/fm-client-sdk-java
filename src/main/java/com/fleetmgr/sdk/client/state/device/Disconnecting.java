package com.fleetmgr.sdk.client.state.device;

import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.client.state.State;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class Disconnecting extends State {

    Disconnecting(State state) {
        super(state);
    }

    @Override
    public State start() {
        backend.getHeartbeatHandler().end();
        backend.closeFacadeChannel();
        listener.onEvent(new FacadeEvent(FacadeEvent.Type.RELEASED));
        return new Disconnected(this);
    }

    @Override
    public State notifyEvent(UserEvent event) {
        switch (event.getType()) {
            default:
                return defaultEventHandle(event.toString());
        }
    }

    @Override
    public State notifyConnection(ConnectionEvent event) {
        switch (event.getType()) {
            default:
                return defaultEventHandle(event.toString());
        }
    }

    @Override
    public String toString() {
        return "Disconnecting";
    }
}
