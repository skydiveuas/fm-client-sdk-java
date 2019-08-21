package com.fleetmgr.sdk.client.state.device;

import com.fleetmgr.interfaces.facade.control.Command;
import com.fleetmgr.interfaces.facade.control.ControlMessage;
import com.fleetmgr.interfaces.facade.control.Response;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.backend.ClientBackend;
import com.fleetmgr.sdk.client.core.CoreClient;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.Error;
import com.fleetmgr.sdk.client.state.State;

import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class Disconnected extends State {

    public Disconnected(Client client,
                        ClientBackend backend,
                        Client.Listener listener) {
        super(client, backend, listener);
    }

    Disconnected(State state) {
        super(state);
    }

    @Override
    public State start() {
        return null;
    }

    @Override
    public State notifyEvent(UserEvent event) {
        switch (event.getType()) {
            case ATTACH:
                return new Connecting(this);

            default:
                return defaultEventHandle(event.toString());
        }
    }

    @Override
    public State notifyConnection(ConnectionEvent event) {
        if (event.getType() == ConnectionEvent.Type.RECEIVED &&
                ((Received)event).getMessage().getCommand() == Command.HEARTBEAT) {
            logger.trace("{}: Ignored: {}", toString(), event.toString());
            return null;
        }
        return defaultEventHandle(event.toString());
    }

    @Override
    public String toString() {
        return "Disconnected";
    }
}
