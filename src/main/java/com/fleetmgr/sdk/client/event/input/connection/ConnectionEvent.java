package com.fleetmgr.sdk.client.event.input.connection;

import com.fleetmgr.sdk.client.event.input.Event;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class ConnectionEvent implements Event {

    public enum Type {
        CLOSED,
        ERROR,
        RECEIVED,
        LOST,
    }

    private Type type;

    public ConnectionEvent(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ConnectionEvent{" +
                "type=" + type +
                '}';
    }
}
