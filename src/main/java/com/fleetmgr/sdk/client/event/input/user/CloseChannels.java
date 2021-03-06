package com.fleetmgr.sdk.client.event.input.user;

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
public class CloseChannels extends UserEvent {

    private final Collection<Long> channelIds;

    public CloseChannels(Collection<Long> channelIds) {
        super(Type.CLOSE_CHANNELS);
        this.channelIds = channelIds;
    }
}
