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

    private final Long channelId;

    public ReleaseControl(Long channelId) {
        super(Type.RELEASE_CONTROL);
        this.channelId = channelId;
    }
}
