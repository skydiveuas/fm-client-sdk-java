package com.fleetmgr.sdk.client.event.output.facade;


/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
public class HandoverAccepted extends FacadeEvent {

    private byte[] data;

    public HandoverAccepted(byte[] data) {
        super(Type.HANDOVER_ACCEPTED);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
