package com.fleetmgr.sdk.client.event.input.user;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
public class RequestControl extends UserEvent {

    private long channelId;

    public RequestControl(long channelId) {
        super(Type.REQUEST_CONTROL);
        this.channelId = channelId;
    }

    public long getChannelId() {
        return channelId;
    }
}
