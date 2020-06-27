package com.fleetmgr.sdk.adapter.client;

import com.fleetmgr.sdk.adapter.Adapter;
import com.fleetmgr.sdk.adapter.configuration.ChannelConfig;
import com.fleetmgr.sdk.adapter.configuration.AdapterConfig;
import com.fleetmgr.sdk.adapter.endpoint.EndpointHandle;
import com.fleetmgr.interfaces.ChannelRequest;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.Pilot;
import com.fleetmgr.sdk.client.event.input.user.Operate;
import com.fleetmgr.sdk.client.event.input.user.ReleaseAccepted;
import com.fleetmgr.sdk.client.event.input.user.ReleaseRejected;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.client.event.output.facade.ReleaseControl;
import com.fleetmgr.sdk.client.event.output.facade.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 17.07.2019
 * Description:
 */
public class PilotAdapter extends Adapter {

    private static final Logger logger = LoggerFactory.getLogger(PilotAdapter.class);

    private final Pilot pilot;

    public PilotAdapter(ExecutorService executor, AdapterConfig adapterConfig) {
        super(executor, adapterConfig);
        this.pilot = new Pilot(executor, getConfiguration(), this, "Pilot");
    }

    @Override
    public void start() {
        logger.info("Starting PilotAdapter");
        LinkedList<ChannelRequest> channels = new LinkedList<>();
        for (ChannelConfig c : adapterConfig.getChannels().values()) {
            ChannelRequest channel = ChannelRequest.newBuilder()
                    .setId(c.getId())
                    .setPriority(c.getPriority())
                    .setProtocol(c.getProtocol())
                    .setSecurity(c.getSecurity())
                    .build();
            channels.add(channel);
        }
        pilot.notifyEvent(new Operate(
                adapterConfig.getDeviceId(),
                adapterConfig.getSerialId(),
                channels));
    }

    @Override
    public void onEvent(FacadeEvent event) {
        super.onEvent(event);
        switch (event.getType()) {
            case RELEASE_CONTROL:
                handleReleaseControl((ReleaseControl) event);
                break;

            case CONTROL_RELEASED:
                break;

            case HANDOVER_ACCEPTED:
                pilot.notifyEvent(new UserEvent(UserEvent.Type.CONTROL_READY));
                break;

            case RELEASED:
                if (!adapterListener.isPresent()) {
                    logger.info("Shutting Adapter due to Edge connection release");
                    System.exit(0);
                }
                break;

            case ERROR:
                if (!adapterListener.isPresent()) {
                    logger.error("Shutting Adapter after failure", ((Error)event).getThrowable());
                    System.exit(-1);
                }
                break;
        }
        adapterListener.ifPresent((l) -> l.onEvent(event));
    }

    private void handleReleaseControl(ReleaseControl event) {
        EndpointHandle ep = endpoints.get(event.getChannelId());
        logger.info("Handling release control with policy {}", ep.getHoPolicy());
        switch (ep.getHoPolicy()) {
            case ALWAYS:
                pilot.notifyEvent(new ReleaseAccepted(null));
                break;

            case NEVER:
                pilot.notifyEvent(new ReleaseRejected("Endpoint configured to not accept HOs"));
                break;

            case IMPL_DEFINED:
                pilot.notifyEvent(ep.handleHoRequest());
                break;
        }
    }

    @Override
    public Client getClient() {
        return pilot;
    }
}
