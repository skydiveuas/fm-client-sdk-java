package com.fleetmgr.sdk.client.state.pilot;

import com.fleetmgr.interfaces.ChannelRequest;
import com.fleetmgr.interfaces.ChannelRequestList;
import com.fleetmgr.interfaces.facade.control.*;
import com.fleetmgr.sdk.client.core.model.FacadeResponse;
import com.fleetmgr.sdk.client.core.model.OperateRequest;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.Error;
import com.fleetmgr.sdk.client.state.State;

import java.util.Collection;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class Connecting extends State {

    private final String device;
    private final String serial;
    private final Collection<ChannelRequest> channels;

    Connecting(State state, String device, String serial, Collection<ChannelRequest> channels) {
        super(state);
        this.device = device;
        this.serial = serial;
        this.channels = channels;
    }

    @Override
    public State start() {
        try {
            FacadeResponse facadeResponse = backend.getCore().operate(
                    OperateRequest.builder()
                            .device(device)
                            .serial(serial)
                            .build());
            backend.openFacadeConnection(facadeResponse);
            send(ClientMessage.newBuilder()
                    .setCommand(Command.SETUP)
                    .setSetupRequest(SetupRequest.newBuilder()
                            .setKey(facadeResponse.getKey())
                            .build())
                    .setChannelsRequest(ChannelRequestList.newBuilder()
                            .addAllChannels(channels)
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
