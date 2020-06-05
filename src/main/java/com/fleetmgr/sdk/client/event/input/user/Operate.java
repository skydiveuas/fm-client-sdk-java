package com.fleetmgr.sdk.client.event.input.user;

import com.fleetmgr.interfaces.ChannelRequest;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
@Getter
@ToString
public class Operate extends UserEvent {

    private final String deviceId;
    private final String serialId;

    private final Collection<ChannelRequest> channelIds;

    public Operate(String deviceId, String serialId, Collection<ChannelRequest> channelIds) {
        super(Type.OPERATE);
        this.deviceId = deviceId;
        this.serialId = serialId;
        this.channelIds = channelIds;
    }
}
