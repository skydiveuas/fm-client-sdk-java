package com.fleetmgr.sdk.client.state.pilot.connected;

import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import com.fleetmgr.sdk.client.event.input.user.ReleaseAccepted;
import com.fleetmgr.sdk.client.event.input.user.ReleaseRejected;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.client.event.output.facade.ReleaseControl;
import com.fleetmgr.sdk.client.state.State;
import com.fleetmgr.interfaces.HandoverData;
import com.fleetmgr.interfaces.facade.control.ClientMessage;
import com.fleetmgr.interfaces.facade.control.Command;
import com.fleetmgr.interfaces.facade.control.ControlMessage;
import com.fleetmgr.interfaces.facade.control.Response;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class ReleasingControl extends State {

    private long channelId;

    ReleasingControl(State state, long channelId) {
        super(state);
        this.channelId = channelId;
    }

    @Override
    public State start() {
        listener.onEvent(new ReleaseControl(channelId));
        return null;
    }

    @Override
    public State notifyEvent(UserEvent event) {
        switch (event.getType()) {
            case RELEASE_ACCEPTED:
                ReleaseAccepted releaseAccepted = (ReleaseAccepted)event;
                send(ClientMessage.newBuilder()
                        .setCommand(Command.RELEASE_CONTROL)
                        .setResponse(Response.ACCEPTED)
                        .setHandoverData(HandoverData.newBuilder()
                                .setHandoverData(new String(releaseAccepted.getData()))
                                .build())
                        .build());
                return null;

            case RELEASE_REJECTED:
                ReleaseRejected releaseRejected = (ReleaseRejected)event;
                send(ClientMessage.newBuilder()
                        .setCommand(Command.RELEASE_CONTROL)
                        .setResponse(Response.REJECTED)
                        .setMessage(releaseRejected.getMessage())
                        .build());
                return new Operating(this);

            default:
                return defaultEventHandle(event.toString());
        }
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
            case CONTROL_RELEASED:
                backend.getChannelsHandler().setOwned(channelId, false);
                listener.onEvent(new FacadeEvent(FacadeEvent.Type.CONTROL_RELEASED));
                send(ClientMessage.newBuilder()
                        .setCommand(Command.CONTROL_RELEASED)
                        .setResponse(Response.ACCEPTED)
                        .build());
                return new Operating(this);

            default:
                return defaultMessageHandle(message);
        }
    }

    @Override
    public String toString() {
        return "ReleasingControl";
    }
}
