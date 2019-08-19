package com.fleetmgr.sdk.adapter;

import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.event.input.user.ReleaseRejected;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.client.event.output.facade.ReleaseControl;
import com.fleetmgr.sdk.client.traffic.Channel;
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
        void send(byte[] data, int size);
        void setExitCode(int exitCode);
    }

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
}
