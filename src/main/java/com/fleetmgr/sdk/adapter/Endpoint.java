package com.fleetmgr.sdk.adapter;

import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.event.input.user.ReleaseRejected;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.07.2019
 * Description:
 */
public abstract class Endpoint {

    public interface Controller {
        Client getClient();
        Long getChannelId();
        void send(byte[] data, int size);
        void onEvent(EndpointEvent endpointEvent);
    }

    protected String prefix = toString();

    @Setter
    @Getter
    private Controller controller;

    public abstract void initialize(String input) throws Exception;

    public abstract void shutdown();

    public abstract void handleData(byte[] data, int size);

    public UserEvent handleHoRequest() {
        return new ReleaseRejected("Endpoint does not support Channel HO," +
                " verify Endpoint object implementation");
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}
