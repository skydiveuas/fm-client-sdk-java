package com.fleetmgr.sdk.client.state.device.connected;

import com.fleetmgr.interfaces.ChannelIndicationList;
import com.fleetmgr.interfaces.ChannelResponse;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.ChannelsClosing;
import com.fleetmgr.sdk.client.traffic.Channel;
import com.fleetmgr.sdk.client.event.output.facade.ChannelsOpened;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.client.state.State;
import com.fleetmgr.interfaces.facade.control.ClientMessage;
import com.fleetmgr.interfaces.facade.control.Command;
import com.fleetmgr.interfaces.facade.control.ControlMessage;
import com.fleetmgr.interfaces.facade.control.Response;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class Flying extends State {

    private Collection<ChannelResponse> initialChannels;

    Flying(State state, Collection<ChannelResponse> initialChannels) {
        super(state);
        this.initialChannels = initialChannels;
    }

    @Override
    public State start() {
        attachChannels(initialChannels);
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
                return handleMessage(((Received)event).getMessage());

            case UNREACHABLE:
                return new Recovering(this);

            default:
                return defaultEventHandle(event.toString());
        }
    }

    private State handleMessage(ControlMessage message) {
        switch (message.getCommand()) {
            case ATTACH_CHANNELS:
                attachChannels(message.getChannelsResponse().getChannelsList());
                return null;

            case RELEASE_CHANNELS:
                releaseChannels(message.getChannelsIndication().getIdsList());
                return null;

            case OPERATION_ENDED:
                listener.onEvent(new ChannelsClosing(backend.getChannelsHandler().getChannels()));
                backend.getChannelsHandler().closeAllChannels();
                listener.onEvent(new FacadeEvent(FacadeEvent.Type.OPERATION_ENDED));
                send(ClientMessage.newBuilder()
                        .setCommand(Command.OPERATION_ENDED)
                        .setResponse(Response.ACCEPTED)
                        .build());
                return new Ready(this);

            default:
                return defaultMessageHandle(message);
        }
    }

    private void attachChannels(Collection<ChannelResponse> channels) {
        Map<Long, Channel> validated =
                backend.getChannelsHandler().validateChannels(channels);
        listener.onEvent(new ChannelsOpened(validated.values()));
        send(ClientMessage.newBuilder()
                .setCommand(Command.ATTACH_CHANNELS)
                .setResponse(Response.ACCEPTED)
                .setChannelsIndication(ChannelIndicationList.newBuilder()
                        .addAllIds(validated.keySet())
                        .build())
                .build());
    }

    private void releaseChannels(List<Long> channelIds) {
        List<Channel> channels =
                backend.getChannelsHandler().getChannels(channelIds);
        listener.onEvent(new ChannelsClosing(channels));
        backend.getChannelsHandler().closeChannels(channelIds);
        send(ClientMessage.newBuilder()
                .setCommand(Command.RELEASE_CHANNELS)
                .setResponse(Response.ACCEPTED)
                .build());
    }

    @Override
    public String toString() {
        return "Flying";
    }
}
