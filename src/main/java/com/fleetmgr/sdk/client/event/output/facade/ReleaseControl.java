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
public class ReleaseControl extends FacadeEvent {

    private final Long channel;

    public ReleaseControl(Long channel) {
        super(Type.RELEASE_CONTROL);
        this.channel = channel;
    }
}
