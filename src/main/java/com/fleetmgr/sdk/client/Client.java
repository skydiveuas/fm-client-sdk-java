package com.fleetmgr.sdk.client;

import com.fleetmgr.interfaces.Location;
import com.fleetmgr.sdk.client.backend.ClientBackend;
import com.fleetmgr.sdk.client.configuration.ClientConfig;
import com.fleetmgr.sdk.client.configuration.Configuration;
import com.fleetmgr.sdk.client.core.CoreClient;
import com.fleetmgr.sdk.client.event.input.Event;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.system.machine.StateMachine;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

/**
 * Created by: Bartosz Nawrot
 * Date: 04.09.2018
 * Description:
 */
public abstract class Client extends StateMachine<Event> {

    public interface Listener {
        Location getLocation();
        void onEvent(FacadeEvent event);
        void log(Level level, String message);
    }

    private Listener listener;

    protected ClientBackend backend;

    Client(ExecutorService executor, String configPath, Listener listener) throws IOException {
        this(executor, ClientConfig.load(configPath), listener);
    }

    Client(ExecutorService executor, Configuration configuration, Listener listener) {
        super(executor, null);
        this.listener = listener;

        CoreClient coreClient = new CoreClient(configuration, this::log);

        this.backend = new ClientBackend(executor, configuration,this, listener, coreClient);
    }

    @Override
    public void log(Level level, String message) {
        listener.log(level, message);
    }
}
