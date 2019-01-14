package com.fleetmgr.sdk.client.event.output.facade;

import com.fleetmgr.sdk.client.traffic.Channel;

import java.util.Collection;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
public class ChannelsOpened extends FacadeEvent {

    private Collection<Channel> channels;

    public ChannelsOpened(Collection<Channel> channels) {
        super(Type.CHANNELS_OPENED);
        this.channels = channels;
    }

    public Collection<Channel> getChannels() {
        return channels;
    }

    @Override
    public String toString() {
        return "ChannelsOpened{" +
                "channels=" + channels +
                '}';
    }
}
