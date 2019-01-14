package com.fleetmgr.sdk.client.event.input.user;

import com.fleetmgr.interfaces.ChannelRequest;

import java.util.Collection;


/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
public class OpenChannels extends UserEvent {

    private Collection<ChannelRequest> channels;

    public OpenChannels(Collection<ChannelRequest> channels) {
        super(Type.OPEN_CHANNELS);
        this.channels = channels;
    }

    public Collection<ChannelRequest> getChannels() {
        return channels;
    }

    @Override
    public String toString() {
        return "OpenChannels{" +
                "channels=" + channels +
                '}';
    }
}
