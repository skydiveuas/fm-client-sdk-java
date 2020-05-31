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
public class Operate extends UserEvent {

    private final String device;
    private final String serial;

    private final Collection<ChannelRequest> channels;

    public Operate(String device, String serial, Collection<ChannelRequest> channels) {
        super(Type.OPERATE);
        this.device = device;
        this.serial = serial;
        this.channels = channels;
    }
}
