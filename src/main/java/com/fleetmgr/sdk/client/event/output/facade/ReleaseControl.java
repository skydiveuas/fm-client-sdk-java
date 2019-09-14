package com.fleetmgr.sdk.client.event.output.facade;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
public class ReleaseControl extends FacadeEvent {

    private long channelId;

    public ReleaseControl(long channelId) {
        super(Type.RELEASE_CONTROL);
        this.channelId = channelId;
    }

    public long getChannelId() {
        return channelId;
    }

    @Override
    public String toString() {
        return "ReleaseControl{" +
                "channelId=" + channelId +
                '}';
    }
}
