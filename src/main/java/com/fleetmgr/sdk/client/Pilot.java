package com.fleetmgr.sdk.client;

import com.fleetmgr.interfaces.ConnectionState;
import com.fleetmgr.interfaces.ListDevicesResponse;
import com.fleetmgr.sdk.client.state.pilot.Disconnected;
import org.cfg4j.provider.ConfigurationProvider;

import java.io.IOException;
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

    public ListDevicesResponse listDevices() throws IOException {
        return backend.getCore().listDevices();
    }

    public ListDevicesResponse listConnectedDevices() throws IOException {
        ListDevicesResponse.Builder builder = ListDevicesResponse.newBuilder();
        ListDevicesResponse response = listDevices();

        response.getDevicesList().stream()
                .filter(device -> device.getConnection() == ConnectionState.CONNECTED)
                .forEach(builder::addDevices);

        return builder.build();
    }
}
