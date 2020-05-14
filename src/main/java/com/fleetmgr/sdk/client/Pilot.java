package com.fleetmgr.sdk.client;

import com.fleetmgr.sdk.client.state.pilot.Disconnected;
import org.cfg4j.provider.ConfigurationProvider;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 04.09.2018
 * Description:
 */
public class Pilot extends Client {

    public Pilot(ExecutorService executor, String configPath,
                 Listener listener, String name) {
        this(executor, loadConfigurationProvider(configPath), listener, name);
    }

    public Pilot(ExecutorService executor, ConfigurationProvider configuration,
                 Listener listener, String name) {
        super(executor, configuration, listener, name);
        setState(new Disconnected(this, backend, listener));
    }

    public List<String> listDevices() throws Exception {
        return backend.getCore().listDevices();
    }

    public List<String> listConnectedDevices() throws Exception {
        return backend.getCore().listConnectedDevices();
    }
}
