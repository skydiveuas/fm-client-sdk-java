package com.fleetmgr.sdk.client;

import java.util.logging.Level;
import com.fleetmgr.sdk.client.backend.ClientBackend;
import com.fleetmgr.sdk.client.core.CoreClient;
import com.fleetmgr.sdk.client.event.input.Event;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.interfaces.Location;
import com.fleetmgr.sdk.system.machine.StateMachine;

import java.util.concurrent.ExecutorService;

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

    Client(String coreAddress, String key, Listener listener, ExecutorService executor) {
        super(executor, null);
        this.listener = listener;

        CoreClient coreClient = new CoreClient(coreAddress, key);

        this.backend = new ClientBackend(this, listener, executor, coreClient);
    }

    @Override
    public void log(Level level, String message) {
        listener.log(level, message);
    }
}
