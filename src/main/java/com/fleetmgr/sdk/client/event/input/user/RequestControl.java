package com.fleetmgr.sdk.client.event.input.user;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
@Getter
@ToString
public class RequestControl extends UserEvent {

    private final Long channel;

    public RequestControl(long channel) {
        super(Type.REQUEST_CONTROL);
        this.channel = channel;
    }
}
