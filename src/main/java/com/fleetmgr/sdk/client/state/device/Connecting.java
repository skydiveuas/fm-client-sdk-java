package com.fleetmgr.sdk.client.state.device;

import com.fleetmgr.interfaces.AttachResponse;
import com.fleetmgr.interfaces.facade.control.*;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.Error;
import com.fleetmgr.sdk.client.state.State;

import java.io.IOException;

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
            AttachResponse attachResponse = backend.getCore().attach();
            backend.openFacadeConnection(
                    attachResponse.getHost(),
                    attachResponse.getPort());
            send(ClientMessage.newBuilder()
                    .setCommand(Command.SETUP)
                    .setAttach(SetupRequest.newBuilder()
                            .setKey(attachResponse.getKey())
                            .build())
                    .build());
        } catch (IOException e) {
            listener.onEvent(new Error(e.getMessage()));
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
                    return new Connected(this);

                } else {
                    listener.onEvent(new Error(message.getMessage()));
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
