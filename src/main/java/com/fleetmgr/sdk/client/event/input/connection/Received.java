package com.fleetmgr.sdk.client.event.input.connection;

import com.fleetmgr.interfaces.facade.control.ControlMessage;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
@Getter
@ToString
public class Received extends ConnectionEvent {

    private final ControlMessage message;

    public Received(ControlMessage message) {
        super(Type.RECEIVED);
        this.message = message;
    }
}
