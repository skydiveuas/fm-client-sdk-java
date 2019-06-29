package com.fleetmgr.sdk.client.backend;

import com.fleetmgr.interfaces.Location;
import com.fleetmgr.interfaces.facade.control.*;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.system.capsule.Timer;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.11.2018
 * Description:
 */
public class HeartbeatHandler {

    private final Logger logger;
    private final Client client;
    private final ClientBackend backend;

    private Timer timer;

    private AtomicLong lastReception;

    HeartbeatHandler(Client client, ClientBackend backend) {
        this.logger = client.getLogger();
        this.client = client;
        this.backend = backend;

        this.lastReception = new AtomicLong(0);
    }

    public void start() {
        logger.info("Starting heartbeat verification task");

        lastReception.set(System.currentTimeMillis());

        long supervisionInterval = backend.getSetupResponse().getSupervisionIntervalMs();
        timer = client.executeEvery(this::onTimeout, supervisionInterval, supervisionInterval);
    }

    public void end() {
        logger.info("Ending heartbeat verification task");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void handleHeartbeat(ControlMessage message) {
        lastReception.set(System.currentTimeMillis());
        Location location = backend.getLocation();
        HeartbeatResponse.Builder builder = HeartbeatResponse.newBuilder()
                .setKey(message.getHeartbeat().getKey());
        if (location != null) {
            builder.setLocation(location);
        }
        backend.send(ClientMessage.newBuilder()
                .setCommand(Command.HEARTBEAT)
                .setResponse(Response.ACCEPTED)
                .setHeartbeat(builder.build())
                .build());
    }

    private void onTimeout() {
        long unreachableTimeoutMs = backend.getSetupResponse().getUnreachableTimeoutMs();
        long silentTime = System.currentTimeMillis() - lastReception.get();
        if (silentTime > unreachableTimeoutMs) {
            client.notifyEvent(new ConnectionEvent(ConnectionEvent.Type.UNREACHABLE));
        }
    }
}
