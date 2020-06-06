package com.fleetmgr.sdk.adapter.main;

import com.fleetmgr.sdk.adapter.Adapter;
import com.fleetmgr.sdk.adapter.ShutdownListener;
import com.fleetmgr.sdk.adapter.client.DeviceAdapter;
import com.fleetmgr.sdk.adapter.client.PilotAdapter;
import com.fleetmgr.sdk.adapter.configuration.AdapterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 16.07.2019
 * Description:
 */
public class AdapterController {

    private static final Logger logger = LoggerFactory.getLogger(AdapterController.class);

    private final AdapterConfig adapterConfig;
    private final ExecutorService executor;

    public AdapterController(AdapterConfig adapterConfig, ExecutorService executor) {
        this.adapterConfig = adapterConfig;
        this.executor = executor;
    }

    public void start() throws Exception {
        start(null);
    }
    
    public void start(ShutdownListener shutdownListener) throws Exception {
        Adapter adapter;
        switch (adapterConfig.getRole()) {
            case DEVICE:
                DeviceAdapter deviceAdapter = new DeviceAdapter(executor, adapterConfig);
                deviceAdapter.setShutdownListener(shutdownListener);
                adapter = deviceAdapter;
                break;

            case PILOT:
                PilotAdapter pilotAdapter = new PilotAdapter(executor, adapterConfig);
                pilotAdapter.setShutdownListener(shutdownListener);
                adapter = pilotAdapter;
                break;

            default:
                throw new Exception("Unexpected Role");
        }
        adapter.setShutdownListener(shutdownListener);
        adapter.start();
    }
}
