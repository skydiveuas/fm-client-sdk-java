package com.fleetmgr.sdk.client.backend;

import com.fleetmgr.interfaces.facade.control.*;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.system.capsule.Timer;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.11.2018
 * Description:
 */
public class HeartbeatHandler {

    private Client client;
    private ClientBackend backend;

    private Timer timer;

    private AtomicLong lastReception;

    HeartbeatHandler(Client client, ClientBackend backend) {
        this.client = client;
        this.backend = backend;

        this.lastReception = new AtomicLong(0);
    }

    public void start() {
        client.log(Level.INFO, "Starting heartbeat verification task");

        lastReception.set(System.currentTimeMillis());

        long supervisionInterval =
                backend.getConfiguration().getProperty("supervision.intervalMs", Long.class);
        timer = client.executeEvery(this::onTimeout, supervisionInterval, supervisionInterval);
    }

    public void end() {
        client.log(Level.INFO, "Ending heartbeat verification task");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void handleHeartbeat(ControlMessage message) {
        lastReception.set(System.currentTimeMillis());
        backend.send(ClientMessage.newBuilder()
                .setCommand(Command.HEARTBEAT)
                .setResponse(Response.ACCEPTED)
                .setHeartbeat(HeartbeatResponse.newBuilder()
                        .setKey(message.getHeartbeat().getKey())
                        .setLocation(backend.getLocation())
                        .build())
                .build());
    }

    private void onTimeout() {
        long supervisionTimeout =
                backend.getConfiguration().getProperty("supervision.timeoutMs", Long.class);
        long silentTime = System.currentTimeMillis() - lastReception.get();
        if (silentTime > supervisionTimeout) {
            client.notifyEvent(new ConnectionEvent(ConnectionEvent.Type.LOST));
        }
    }
}
