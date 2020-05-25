package com.fleetmgr.adapter.endpoint;

import com.fleetmgr.adapter.Adapter;
import com.fleetmgr.adapter.configuration.ChannelConfig;
import com.fleetmgr.adapter.configuration.EndpointConfig;
import com.fleetmgr.adapter.configuration.FilterConfig;
import com.fleetmgr.sdk.adapter.Filter;
import com.fleetmgr.interfaces.Location;
import com.fleetmgr.sdk.adapter.Endpoint;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.event.input.user.ReleaseRejected;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.fleetmgr.sdk.client.traffic.Channel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by: Bartosz Nawrot
 * Date: 16.07.2019
 * Description:
 */
public class EndpointHandle implements
        Channel.Listener,
        Endpoint.Controller,
        Filter.Listener {

    private static final Logger logger = LoggerFactory.getLogger(EndpointHandle.class);

    private Adapter adapter;
    private Channel channel;
    private LinkedList<Filter> filters;

    private Endpoint endpoint;
    private AtomicBoolean endpointReady;

    @Getter
    private ChannelConfig.HoPolicy hoPolicy;

    public EndpointHandle(Adapter adapter, Channel channel) {
        this.adapter = adapter;
        this.channel = channel;
        this.filters = new LinkedList<>();
        this.endpointReady = new AtomicBoolean(false);
        this.channel.setListener(this);
        this.hoPolicy = ChannelConfig.HoPolicy.ALWAYS;
    }

    public void initialize(ChannelConfig channelConfig) {
        hoPolicy = channelConfig.getHoPolicy();
        buildImplementation(channelConfig.getEndpoint());
        attachFilers(channelConfig.getFilter());
    }

    public void shutdown() {
        if (endpointReady.get()) {
            endpoint.shutdown();
        }
        for (Filter f : filters) {
            f.shutdown();
        }
    }

    public UserEvent handleHoRequest() {
        if (endpointReady.get()) {
            return endpoint.handleHoRequest();
        } else {
            return new ReleaseRejected("Dummy Endpoint does not support Channel HO");
        }
    }

    @Override
    public Client getClient() {
        return adapter.getClient();
    }

    @Override
    public void send(byte[] data, int size) {
        for (Filter f : filters) {
            f.onSent(data, size);
        }
        try {
            logger.trace("Channel id:{} sending {} bytes", channel.getId(), size);
            channel.send(data, size);
        } catch (IOException e) {
            logger.error("Could not send data over {}", this, e);
        }
    }

    @Override
    public void onLocation(Location location) {
        adapter.setLocation(location);
    }

    @Override
    public void onReceived(Channel channel, byte[] data, int size) {
        logger.trace("Channel id:{} received {} bytes", channel.getId(), size);
        for (Filter f : filters) {
            f.onReceived(data, size);
        }
        if (endpointReady.get()) {
            endpoint.handleData(data, size);
        }
    }

    @Override
    public void onClosed(Channel channel) {
        logger.debug("Channel id:{} closed", channel.getId());
    }

    private void buildImplementation(EndpointConfig endpointConfig) {
        if (endpointConfig != null) {
            try {
                endpoint = (Endpoint) Class
                        .forName(endpointConfig.getObject())
                        .newInstance();
                endpoint.setController(this);
            } catch (Exception e) {
                logger.error("Could not create Endpoint implementation", e);
                return;
            }

            try {
                String input = endpointConfig.getInput();
                logger.info("Initialize {} with {}", endpoint.getClass().getCanonicalName(), input);
                endpoint.initialize(input);
            } catch (Exception e) {
                logger.error("Could not initialize Endpoint implementation", e);
                return;
            }

            endpointReady.set(true);
        }
    }

    private void attachFilers(FilterConfig filterConfig) {
        if (filterConfig != null) {
            try {
                Filter filter = (Filter) Class
                        .forName(filterConfig.getObject())
                        .newInstance();
                filter.initialize(filterConfig.getInput());
                filter.setListener(this);
                filters.add(filter);
                logger.info("Filter implementation {} created and initialized with {}",
                        filterConfig.getObject(), filterConfig.getInput());
            } catch (Exception e) {
                logger.error("Could not create Filter implementation", e);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(channel.getId()).append("]:");
        if (endpoint != null) {
            stringBuilder.append(endpoint.getClass().getCanonicalName());
            if (!endpointReady.get()) {
                stringBuilder.append("(uninitialized)");
            }
        } else {
            stringBuilder.append("DummyEndpoint");
        }
        return stringBuilder.toString();
    }
}
