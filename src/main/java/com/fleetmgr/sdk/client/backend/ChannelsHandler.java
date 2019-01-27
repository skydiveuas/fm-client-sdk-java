package com.fleetmgr.sdk.client.backend;

import com.fleetmgr.interfaces.ChannelResponse;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.traffic.Channel;
import com.fleetmgr.sdk.client.traffic.ChannelImpl;
import com.fleetmgr.sdk.client.traffic.socket.Socket;
import com.fleetmgr.sdk.client.traffic.socket.TcpSocket;
import com.fleetmgr.sdk.client.traffic.socket.TlsTcpSocket;
import com.fleetmgr.sdk.client.traffic.socket.UdpSocket;
import java.util.logging.Level;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 16.12.2018
 * Description:
 */
public class ChannelsHandler {

    private Client client;

    private ExecutorService executor;

    private HashMap<Long, ChannelImpl> channels;

    ChannelsHandler(Client client, ExecutorService executor) {
        this.client = client;
        this.executor = executor;

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
                log(Level.INFO, "Opening channel, id: " + c.getId());

                Socket socket = buildSocket(c);
                ChannelImpl channel = new ChannelImpl(c.getId(), socket);
                channel.open(c.getHost(), c.getPort(), c.getKey());

                channels.put(c.getId(), channel);
                opened.put(c.getId(), channel);

                log(Level.INFO, "Channel id: " + c.getId() + " validated");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return opened;
    }

    private Socket buildSocket(ChannelResponse c) {
        switch (c.getProtocol()) {
            case UDP:
                switch (c.getSecurity()) {
                    case PLAIN_TEXT:
                        return new UdpSocket(executor);

                    case TLS:
                        log(Level.SEVERE, "UDP Encryption not supported");
                        return null;
                }
                break;

            case TCP:
                switch (c.getSecurity()) {
                    case PLAIN_TEXT:
                        return new TcpSocket(executor);

                    case TLS:
                        return new TlsTcpSocket(executor);
                }
                break;
        }
        log(Level.SEVERE, "Unexpected channel type: " +
                c.getProtocol() + ":" + c.getSecurity());
        return null;
    }

    public void closeChannels(Collection<Long> channels) {
        for (Long c : channels) {
            log(Level.INFO, "Closing channel, id: " + c);
            ChannelImpl s = this.channels.remove(c);
            if (s != null) {
                s.close();
            } else {
                log(Level.INFO, "Warning, trying to close not existing channel, id: " + c);
            }
        }
    }

    public void closeAllChannels() {
        for (ChannelImpl c : channels.values()) {
            log(Level.INFO, "Closing channel id: " + c.getId());
            c.close();
        }
        channels.clear();
    }

    public void setOwned(Collection<Long> owned) {
        for (Long id : owned) {
            log(Level.INFO, "Setting channel id: " + id + " as owned");
            channels.get(id).setOwned(true);
        }
    }

    public void setOwned(long channelId, boolean owned) {
        channels.get(channelId).setOwned(owned);
    }

    public void log(Level level, String message) {
        client.log(level, message);
    }
}
