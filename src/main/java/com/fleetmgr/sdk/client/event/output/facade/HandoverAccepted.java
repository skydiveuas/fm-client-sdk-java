package com.fleetmgr.sdk.client.event.output.facade;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
@Getter
@ToString
public class HandoverAccepted extends FacadeEvent {

    private final byte[] data;

    public HandoverAccepted(byte[] data) {
        super(Type.HANDOVER_ACCEPTED);
        this.data = data;
    }
}
