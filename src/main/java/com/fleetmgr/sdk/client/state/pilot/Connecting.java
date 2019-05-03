package com.fleetmgr.sdk.client.state.pilot;

import com.fleetmgr.interfaces.*;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.Error;
import com.fleetmgr.sdk.client.state.State;
import com.fleetmgr.interfaces.facade.control.*;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class Connecting extends State {

    private long deviceId;
    private Collection<ChannelRequest> channels;

    private Role role;

    Connecting(State state, long deviceId, Collection<ChannelRequest> channels) {
        super(state);
        this.deviceId = deviceId;
        this.channels = channels;
    }

    @Override
    public State start() {
        try {
            OperateResponse operateResponse = backend.getCore().operate(
                    OperateRequest.newBuilder()
                    .setRequestedDeviceId(deviceId)
                    .build());
            backend.openFacadeConnection(
                    operateResponse.getHost(),
                    operateResponse.getPort());
            send(ClientMessage.newBuilder()
                    .setCommand(Command.SETUP)
                    .setAttach(SetupRequest.newBuilder()
                            .setKey(operateResponse.getKey())
                            .build())
                    .setChannelsRequest(ChannelRequestList.newBuilder()
                            .addAllChannels(channels)
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
                    return new Connected(this,
                            message.getChannelsResponse().getChannelsList());

                } else {
                    return defaultMessageHandle(message);
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
