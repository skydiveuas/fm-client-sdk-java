package com.fleetmgr.sdk.client.backend;

import com.fleetmgr.interfaces.ChannelResponse;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.traffic.Channel;
import com.fleetmgr.sdk.client.traffic.ChannelImpl;
import com.fleetmgr.sdk.client.traffic.socket.Socket;
import com.fleetmgr.sdk.client.traffic.socket.TcpSocket;
import com.fleetmgr.sdk.client.traffic.socket.TlsTcpSocket;
import com.fleetmgr.sdk.client.traffic.socket.UdpSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 16.12.2018
 * Description:
 */
public class ChannelsHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChannelsHandler.class);

    private ExecutorService executor;
    private HashMap<Long, ChannelImpl> channels;

    ChannelsHandler(ClientBackend backend) {
        this.executor = backend.getExecutor();
        this.channels = new HashMap<>();
    }

    public Collection<Channel> getChannels() {
        return new LinkedList<>(channels.values());
    }

    public List<Channel> getChannels(Collection<Long> channels) {
        LinkedList<Channel> result = new LinkedList<>();
        for (Long id : channels) {
            result.add(this.channels.get(id));
        }
        return result;
    }

    public Collection<Long> getChannelsIds() {
        return channels.keySet();
    }

    public Map<Long, Channel> validateChannels(Collection<ChannelResponse> toValidate) {
        Map<Long, Channel> opened = new HashMap<>();
        for (ChannelResponse c : toValidate) {
            try {
                logger.info("Opening channel, id: {}", c.getId());

                Socket socket = buildSocket(c);
                ChannelImpl channel = new ChannelImpl(c.getId(), socket);
                channel.open(c.getHost(), c.getPort(), c.getKey());

                channels.put(c.getId(), channel);
                opened.put(c.getId(), channel);

                logger.info("Channel id: {} validated", c.getId());

            } catch (Exception e) {
                logger.error("Could not validate channel", e);
            }
        }
        return opened;
    }

    private Socket buildSocket(ChannelResponse parameters) {
        switch (parameters.getProtocol()) {
            case UDP:
                switch (parameters.getSecurity()) {
                    case PLAIN_TEXT:
                        return new UdpSocket(executor);

                    case TLS:
                        logger.error("UDP Encryption not supported");
                        return null;
                }
                break;

            case TCP:
                switch (parameters.getSecurity()) {
                    case PLAIN_TEXT:
                        return new TcpSocket(executor);

                    case TLS:
                        return new TlsTcpSocket(executor);
                }
                break;
        }
        logger.error("Unexpected channel type: {}:{}",
                parameters.getProtocol(), parameters.getSecurity());
        return null;
    }

    public void closeChannels(Collection<Long> channels) {
        for (Long c : channels) {
            logger.info("Closing channel, id: {}", c);
            ChannelImpl s = this.channels.remove(c);
            if (s != null) {
                s.close();
            } else {
                logger.warn("Trying to close not existing channel, id: {}", c);
            }
        }
    }

    public void closeAllChannels() {
        for (ChannelImpl c : channels.values()) {
            logger.info("Closing channel id: {}", c.getId());
            c.close();
        }
        channels.clear();
    }

    public void setOwned(Collection<Long> owned) {
        for (Long id : owned) {
            logger.info("Setting channel id: {} as owned", id);
            channels.get(id).setOwned(true);
        }
    }

    public void setOwned(long channelId, boolean owned) {
        channels.get(channelId).setOwned(owned);
    }
}
