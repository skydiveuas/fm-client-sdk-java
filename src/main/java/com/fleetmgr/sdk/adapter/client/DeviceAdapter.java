package com.fleetmgr.sdk.adapter.client;

import com.fleetmgr.sdk.adapter.Adapter;
import com.fleetmgr.sdk.adapter.configuration.AdapterConfig;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.Device;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 17.07.2019
 * Description:
 */
public class DeviceAdapter extends Adapter {

    private static final Logger logger = LoggerFactory.getLogger(DeviceAdapter.class);

    private final Device device;

    public DeviceAdapter(ExecutorService executor, AdapterConfig adapterConfig) {
        super(executor, adapterConfig);
        this.device = new Device(executor, getConfiguration(), this, "Device");
    }

    @Override
    public void start() {
        logger.info("Starting DeviceAdapter");
        device.notifyEvent(new UserEvent(UserEvent.Type.ATTACH));
    }

    @Override
    public void onEvent(FacadeEvent event) {
        super.onEvent(event);
        switch (event.getType()) {
            case RELEASED:
            case ERROR:
                if (!adapterListener.isPresent()) {
                    executor.execute(() -> {
                        logger.info("Waiting 30s before reconnection");
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException ignore) {
                        }
                        device.notifyEvent(new UserEvent(UserEvent.Type.ATTACH));
                    });
                }
                break;
        }
        adapterListener.ifPresent(l -> l.onFacadeEvent(event));
    }

    @Override
    public Client getClient() {
        return device;
    }
}
