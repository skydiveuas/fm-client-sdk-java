package com.fleetmgr.sdk.client.event.output.facade;

import com.fleetmgr.sdk.client.traffic.Channel;

import java.util.Collection;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
public class ChannelsClosing extends FacadeEvent {

    private Collection<Channel> channels;

    public ChannelsClosing(Collection<Channel> channels) {
        super(Type.CHANNELS_CLOSING);
        this.channels = channels;
    }

    public Collection<Channel> getChannels() {
        return channels;
    }

    @Override
    public String toString() {
        return "ChannelsClosing{" +
                "channels=" + channels +
                '}';
    }
}
