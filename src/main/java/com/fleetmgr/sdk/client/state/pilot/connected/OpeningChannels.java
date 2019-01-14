package com.fleetmgr.sdk.client.state.pilot.connected;

import com.fleetmgr.interfaces.ChannelRequest;
import com.fleetmgr.interfaces.ChannelRequestList;
import com.fleetmgr.interfaces.facade.control.ClientMessage;
import com.fleetmgr.interfaces.facade.control.Command;
import com.fleetmgr.interfaces.facade.control.ControlMessage;
import com.fleetmgr.interfaces.facade.control.Response;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.ProcedureRejected;
import com.fleetmgr.sdk.client.state.State;

import java.util.Collection;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.12.2018
 * Description:
 */
public class OpeningChannels extends State  {

    private Collection<ChannelRequest> channelsToOpen;

    OpeningChannels(State state, Collection<ChannelRequest> channelsToClose) {
        super(state);
        this.channelsToOpen = channelsToClose;
    }

    @Override
    public State start() {
        send(ClientMessage.newBuilder()
                .setCommand(Command.ADD_CHANNELS)
                .setChannelsRequest(ChannelRequestList.newBuilder()
                        .addAllChannels(channelsToOpen)
                        .build())
                .build());
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
            case ADD_CHANNELS:
                if (message.getResponse() == Response.ACCEPTED) {
                    return new ValidatingChannels(this,
                            message.getChannelsResponse().getChannelsList());

                } else {
                    listener.onEvent(new ProcedureRejected(Command.ADD_CHANNELS,
                            message.getMessage()));
                    return new Operating(this);
                }

            default:
                return defaultMessageHandle(message);
        }
    }

    @Override
    public String toString() {
        return "OpeningChannels";
    }
}
