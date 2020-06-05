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
public class OpenChannels extends UserEvent {

    private final Collection<ChannelRequest> channelIds;

    public OpenChannels(Collection<ChannelRequest> channelIds) {
        super(Type.OPEN_CHANNELS);
        this.channelIds = channelIds;
    }
}
