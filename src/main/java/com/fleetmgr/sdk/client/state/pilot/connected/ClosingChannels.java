package com.fleetmgr.sdk.client.state.pilot.connected;

import java.util.logging.Level;
import com.fleetmgr.interfaces.ChannelIndicationList;
import com.fleetmgr.interfaces.facade.control.ClientMessage;
import com.fleetmgr.interfaces.facade.control.Command;
import com.fleetmgr.interfaces.facade.control.ControlMessage;
import com.fleetmgr.interfaces.facade.control.Response;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.ChannelsClosing;
import com.fleetmgr.sdk.client.event.output.facade.ProcedureRejected;
import com.fleetmgr.sdk.client.state.State;

import java.util.Collection;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.12.2018
 * Description:
 */
public class ClosingChannels extends State {

    private Collection<Long> channelsToClose;

    ClosingChannels(State state, Collection<Long> channelsToClose) {
        super(state);
        this.channelsToClose = channelsToClose;
    }

    @Override
    public State start() {
        // this call can cause channels close or release
        send(ClientMessage.newBuilder()
                .setCommand(Command.REMOVE_CHANNELS)
                .setChannelsIndication(ChannelIndicationList.newBuilder()
                        .addAllIds(channelsToClose)
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
            case REMOVE_CHANNELS:
                if (message.getResponse() == Response.ACCEPTED) {
                    listener.onEvent(new ChannelsClosing(
                            backend.getChannelsHandler().getChannels(channelsToClose)));
                    backend.getChannelsHandler().closeChannels(channelsToClose);
                    send(ClientMessage.newBuilder()
                            .setCommand(Command.REMOVE_CHANNELS)
                            .setResponse(Response.ACCEPTED)
                            .build());
                    return new Operating(this);

                } else {
                    listener.onEvent(new ProcedureRejected(Command.REMOVE_CHANNELS,
                            message.getMessage()));
                    return new Operating(this);
                }

            case RELEASE:
                listener.onEvent(new ChannelsClosing(
                        backend.getChannelsHandler().getChannels()));
                backend.getChannelsHandler().closeAllChannels();
                send(ClientMessage.newBuilder()
                        .setCommand(Command.RELEASE)
                        .setResponse(Response.ACCEPTED)
                        .build());
                return new Released(this);

            case HEARTBEAT:
                log(Level.INFO, "Heartbeat ignored");
                return null;

            default:
                return defaultMessageHandle(message);
        }
    }

    @Override
    public String toString() {
        return "ClosingChannels";
    }
}
