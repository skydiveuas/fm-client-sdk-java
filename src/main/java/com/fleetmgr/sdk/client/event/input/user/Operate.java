package com.fleetmgr.sdk.client.event.input.user;

import com.fleetmgr.interfaces.ChannelRequest;

import java.util.Collection;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
public class Operate extends UserEvent {

    private String deviceId;

    private Collection<ChannelRequest> channels;

    public Operate(String deviceId, Collection<ChannelRequest> channels) {
        super(Type.OPERATE);
        this.deviceId = deviceId;
        this.channels = channels;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Collection<ChannelRequest> getChannels() {
        return channels;
    }

    @Override
    public String toString() {
        return "Operate{" +
                "deviceId=" + deviceId +
                ", channels=" + channels +
                '}';
    }
}
