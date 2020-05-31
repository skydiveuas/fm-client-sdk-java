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

    private final Collection<ChannelRequest> channels;

    public OpenChannels(Collection<ChannelRequest> channels) {
        super(Type.OPEN_CHANNELS);
        this.channels = channels;
    }
}
