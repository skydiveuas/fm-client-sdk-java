package com.fleetmgr.sdk.client;

import com.fleetmgr.sdk.client.configuration.ClientConfig;
import com.fleetmgr.sdk.client.configuration.Configuration;
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

    public Device(ExecutorService executor, Configuration configuration, Listener listener) {
        super(executor, configuration, listener);
        setState(new Disconnected(this, backend, listener));
    }
}
