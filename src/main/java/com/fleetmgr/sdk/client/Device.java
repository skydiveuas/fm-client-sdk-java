package com.fleetmgr.sdk.client;

import com.fleetmgr.sdk.client.state.device.Disconnected;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 04.09.2018
 * Description:
 */
public class Device extends Client {

    public Device(ExecutorService executor, String configPath, Listener listener) throws IOException {
        this(executor, ClientConfig.load(configPath), listener);
    }

    public Device(ExecutorService executor, ClientConfig clientConfig, Listener listener) {
        super(executor, clientConfig, listener);
        setState(new Disconnected(this, backend, listener));
    }
}
