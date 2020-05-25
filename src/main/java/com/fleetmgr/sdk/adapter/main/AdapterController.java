package com.fleetmgr.sdk.adapter.main;

import com.fleetmgr.sdk.adapter.Adapter;
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
        Adapter adapter;
        switch (adapterConfig.getRole()) {
            case DEVICE:
                adapter = new DeviceAdapter(executor, adapterConfig);
                break;

            case PILOT:
                adapter = new PilotAdapter(executor, adapterConfig);
                break;

            default:
                throw new Exception("Unexpected Role");
        }
        adapter.start();
    }
}
