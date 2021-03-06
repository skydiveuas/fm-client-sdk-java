package com.fleetmgr.sdk.client.event.output.facade;

import com.fleetmgr.sdk.client.traffic.Channel;
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
public class ChannelsClosing extends FacadeEvent {

    private final Collection<Channel> channelIds;

    public ChannelsClosing(Collection<Channel> channelIds) {
        super(Type.CHANNELS_CLOSING);
        this.channelIds = channelIds;
    }
}
