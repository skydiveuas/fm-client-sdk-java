package com.fleetmgr.sdk.client.event.input.connection;

import com.fleetmgr.sdk.client.event.input.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
@Getter
@ToString
public class ConnectionEvent implements Event {

    public enum Type {
        RECEIVED,
        UNREACHABLE,
        CONNECTION_DROPPED
    }

    private final Type type;

    public ConnectionEvent(Type type) {
        this.type = type;
    }
}
