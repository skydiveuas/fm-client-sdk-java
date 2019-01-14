package com.fleetmgr.sdk.client.state.pilot.connected;

import com.fleetmgr.interfaces.ChannelIndicationList;
import com.fleetmgr.interfaces.ChannelResponse;
import com.fleetmgr.interfaces.facade.control.ClientMessage;
import com.fleetmgr.interfaces.facade.control.Command;
import com.fleetmgr.interfaces.facade.control.ControlMessage;
import com.fleetmgr.interfaces.facade.control.Response;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.ChannelsOpened;
import com.fleetmgr.sdk.client.state.State;
import com.fleetmgr.sdk.client.traffic.Channel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by: Bartosz Nawrot
 * Date: 24.09.2018
 * Description:
 */
public class ValidatingChannels extends State {

    private List<ChannelResponse> channels;

    private Map<Long, Channel> validated;

    public ValidatingChannels(State state, List<ChannelResponse> channels) {
        super(state);
        this.channels = channels;
    }

    @Override
    public State start() {
        validated = backend.getChannelsHandler().validateChannels(channels);
        send(ClientMessage.newBuilder()
                .setCommand(Command.CHANNELS_READY)
                .setChannelsIndication(ChannelIndicationList.newBuilder()
                        .addAllIds(validated.keySet())
                        .build())
                .build());
        return null;
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
            case RECEIVED:
                return handleMessage(((Received) event).getMessage());

            default:
                return defaultEventHandle(event.toString());
        }
    }

    private State handleMessage(ControlMessage message) {
        switch (message.getCommand()) {
            case CHANNELS_READY:
                if (message.getResponse() == Response.ACCEPTED) {
                    Collection<Long> owned = message.getChannelsIndication().getIdsList();
                    backend.getChannelsHandler().setOwned(owned);
                    listener.onEvent(new ChannelsOpened(validated.values()));
                    return new Operating(this);

                } else {
                    return defaultMessageHandle(message);
                }

            default:
                return defaultMessageHandle(message);
        }
    }

    @Override
    public String toString() {
        return "ValidatingChannels";
    }
}
