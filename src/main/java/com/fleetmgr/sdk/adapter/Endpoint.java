package com.fleetmgr.sdk.adapter;

import com.fleetmgr.sdk.client.Client;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.07.2019
 * Description:
 */
public abstract class Endpoint {

    public interface Controller {
        void send(byte[] data, int size);
        Client getClient();
    }

    @Setter
    @Getter
    private Controller controller;

    public abstract void initialize(String input) throws Exception;

    public abstract void shutdown();

    public abstract void handleData(byte[] data, int size);
}
