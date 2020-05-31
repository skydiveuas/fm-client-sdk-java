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
public class ReleaseRejected extends UserEvent {

    private final String message;

    public ReleaseRejected(String message) {
        super(Type.RELEASE_REJECTED);
        this.message = message;
    }
}
