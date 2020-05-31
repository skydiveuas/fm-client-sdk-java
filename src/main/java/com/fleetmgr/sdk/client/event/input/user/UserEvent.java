package com.fleetmgr.sdk.client.event.input.user;

import com.fleetmgr.sdk.client.event.input.Event;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
@Getter
@ToString
public class UserEvent implements Event {

    public enum Type {
        ATTACH,
        RELEASE,
        OPERATE,
        REQUEST_CONTROL,
        RELEASE_ACCEPTED,
        RELEASE_REJECTED,
        CONTROL_READY,
        OPEN_CHANNELS,
        CLOSE_CHANNELS,
    }

    private final Type type;

    public UserEvent(Type type) {
        this.type = type;
    }
}
