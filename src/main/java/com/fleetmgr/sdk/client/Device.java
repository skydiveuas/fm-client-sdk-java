package com.fleetmgr.sdk.client;

import com.fleetmgr.sdk.client.state.device.Disconnected;
import org.cfg4j.provider.ConfigurationProvider;

import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 04.09.2018
 * Description:
 */
public class Device extends Client {

    public Device(ExecutorService executor, String configPath, Listener listener) {
        this(executor, loadConfigurationProvider(configPath), listener);
    }

    public Device(ExecutorService executor, ConfigurationProvider configuration, Listener listener) {
        super(executor, configuration, listener);
        setState(new Disconnected(this, backend, listener));
    }
}
