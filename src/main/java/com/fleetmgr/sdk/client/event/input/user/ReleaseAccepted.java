package com.fleetmgr.sdk.client.event.input.user;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
public class ReleaseAccepted extends UserEvent {

    private byte[] data;

    public ReleaseAccepted(byte[] data) {
        super(Type.RELEASE_ACCEPTED);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
