package com.fleetmgr.sdk.client.state.device;

import com.fleetmgr.interfaces.facade.control.*;
import com.fleetmgr.sdk.client.core.model.FacadeResponse;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.Error;
import com.fleetmgr.sdk.client.state.State;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class Connecting extends State {

    Connecting(State state) {
        super(state);
    }

    @Override
    public State start() {
        try {
            FacadeResponse facadeResponse = backend.getCore().attach();
            backend.openFacadeConnection(facadeResponse);
            send(ClientMessage.newBuilder()
                    .setCommand(Command.SETUP)
                    .setSetupRequest(SetupRequest.newBuilder()
                            .setKey(facadeResponse.getKey())
                            .build())
                    .build());
        } catch (Exception e) {
            listener.onEvent(new Error(new Throwable(e)));
            return new Disconnected(this);
        }
        return null;
    }

    @Override
    public State notifyEvent(UserEvent event) {
        return defaultEventHandle(event.toString());
    }

    @Override
    public State notifyConnection(ConnectionEvent event) {
        switch (event.getType()) {
            case RECEIVED:
                return handleMessage(((Received)event).getMessage());

            default:
                return defaultEventHandle(event.toString());
        }
    }

    private State handleMessage(ControlMessage message) {
        switch (message.getCommand()) {
            case SETUP:
                if (message.getResponse() == Response.ACCEPTED) {
                    backend.setSetupResponse(message.getSetupResponse());
                    return new Connected(this);

                } else {
                    listener.onEvent(new Error(new Throwable(message.getMessage())));
                    return new Disconnecting(this);
                }
                
            default:
                return defaultMessageHandle(message);
        }
    }

    @Override
    public String toString() {
        return "Connecting";
    }
}
