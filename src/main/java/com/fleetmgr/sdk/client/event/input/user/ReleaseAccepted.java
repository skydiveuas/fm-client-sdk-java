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
public class ReleaseAccepted extends UserEvent {

    private byte[] data;

    public ReleaseAccepted(byte[] data) {
        super(Type.RELEASE_ACCEPTED);
        this.data = data;
        if (this.data == null) {
            this.data = new byte[0];
        }
    }
}
