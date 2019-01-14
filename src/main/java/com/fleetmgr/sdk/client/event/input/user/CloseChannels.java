package com.fleetmgr.sdk.client.event.input.user;

import java.util.Collection;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
public class CloseChannels extends UserEvent {

    private Collection<Long> channels;

    public CloseChannels(Collection<Long> channels) {
        super(Type.CLOSE_CHANNELS);
        this.channels = channels;
    }

    public Collection<Long> getChannels() {
        return channels;
    }

    @Override
    public String toString() {
        return "CloseChannels{" +
                "channels=" + channels +
                '}';
    }
}
