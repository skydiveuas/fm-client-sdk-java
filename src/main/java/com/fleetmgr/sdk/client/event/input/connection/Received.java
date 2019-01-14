package com.fleetmgr.sdk.client.event.input.connection;

import com.fleetmgr.interfaces.facade.control.ControlMessage;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class Received extends ConnectionEvent {

    private ControlMessage message;

    public Received(ControlMessage message) {
        super(Type.RECEIVED);
        this.message = message;
    }

    public ControlMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Received{" +
                "message=\n" + message +
                '}';
    }
}
