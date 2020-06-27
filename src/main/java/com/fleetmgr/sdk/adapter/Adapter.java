package com.fleetmgr.sdk.adapter;

import com.fleetmgr.sdk.adapter.api.Service;
import com.fleetmgr.sdk.adapter.configuration.AdapterConfig;
import com.fleetmgr.sdk.adapter.configuration.ChannelConfig;
import com.fleetmgr.sdk.adapter.endpoint.EndpointHandle;
import com.fleetmgr.interfaces.Location;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.event.output.facade.ChannelsClosing;
import com.fleetmgr.sdk.client.event.output.facade.ChannelsOpened;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.client.traffic.Channel;
import lombok.Getter;
import lombok.Setter;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Created by: Bartosz Nawrot
 * Date: 17.07.2019
 * Description:
 */
public abstract class Adapter implements
        Client.Listener,
        ConfigurationSource {

    private static final Logger logger = LoggerFactory.getLogger(Adapter.class);

    @Getter
    protected AdapterConfig adapterConfig;
    @Getter
    protected ExecutorService executor;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected Optional<AdapterListener> adapterListener = Optional.empty();

    @Getter
    protected HashMap<Long, EndpointHandle> endpoints;

    @Setter
    @Getter
    protected Location location;

    protected Adapter(ExecutorService executor, AdapterConfig adapterConfig) {
        this.executor = executor;
        this.adapterConfig = adapterConfig;
        this.endpoints = new HashMap<>();

        if (adapterConfig.getApi() != null) {
            Service apiService = new Service(this);
            try {
                apiService.initialize(executor);
            } catch (IOException e) {
                logger.warn("Could not initialize API service", e);
            }
        }

        if (adapterConfig.getLocation() != null) {
            location = Location.newBuilder()
                    .setLatitude(adapterConfig.getLocation().getLat())
                    .setLongitude(adapterConfig.getLocation().getLon())
                    .setAltitude(adapterConfig.getLocation().getAlt())
                    .build();
        }
    }

    public void setAdapterListener(AdapterListener listener) {
        adapterListener = Optional.of(listener);
    }

    public abstract void start() throws Exception;

    public abstract Client getClient();

    @Override
    public void onEvent(FacadeEvent event) {
        logger.info("Handling: {}", event);
        switch (event.getType()) {
            case CHANNELS_OPENED:
                handleChannelsOpened((ChannelsOpened) event);
                break;

            case CHANNELS_CLOSING:
                handleChannelsClosing((ChannelsClosing) event);
                break;
        }
    }

    private void handleChannelsOpened(ChannelsOpened event) {
        for (Channel c : event.getChannelIds()) {
            List<ChannelConfig> list =
                    adapterConfig.getChannels().values().stream()
                            .filter(o -> o.getId().equals(c.getId()))
                            .collect(Collectors.toList());

            ChannelConfig channelConfig = null;
            if (!list.isEmpty()) {
                channelConfig = list.get(0);
            } else {
                logger.warn("Could not find definition for Channel[{}]", c.getId());
            }

            EndpointHandle endpointHandle = new EndpointHandle(this, c);
            endpoints.put(c.getId(), endpointHandle);

            if (channelConfig != null) {
                endpointHandle.initialize(channelConfig);
            }

            logger.info("Configured {}", endpointHandle);
        }
    }

    private void handleChannelsClosing(ChannelsClosing event) {
        for (Channel c : event.getChannelIds()) {
            endpoints.computeIfPresent(c.getId(), (k, v) -> {
                logger.info("Shutdown {}", v);
                v.shutdown();
                return v;
            });
        }
    }

    public ConfigurationProvider getConfiguration() {
        return new ConfigurationProviderBuilder()
                .withConfigurationSource(this)
                .build();
    }

    @Override
    public Properties getConfiguration(Environment environment) {
        Properties p = new Properties();
        p.setProperty("core.address", adapterConfig.getCore().getAddress());
        p.setProperty("core.apiKey", adapterConfig.getCore().getApiKey());
        p.setProperty("facade.useTls", String.valueOf(adapterConfig.getFacade().isUseTls()));
        if (adapterConfig.getFacade().getCertPath() != null) {
            p.setProperty("facade.certPath", adapterConfig.getFacade().getCertPath());
        }
        return p;
    }

    @Override
    public void init() {
    }

    @Override
    public void reload() {
    }
}
