package com.fleetmgr.sdk.client.traffic.socket;

import com.fleetmgr.interfaces.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.11.2018
 * Description:
 */
public abstract class Socket {

    static final int BUFFER_SIZE = 0xFFF;

    protected static final Logger logger = LoggerFactory.getLogger(Socket.class);

    public interface Listener {
        void onReceived(byte[] data, int size);
        void onClosed();
    }

    protected Listener listener;

    public void setListener(Listener listener)
    {
        this.listener = listener;
    }

    public abstract void connect(String ip, int port) throws Exception;

    public abstract void startReading();

    public abstract int readBlocking(byte[] data) throws IOException;

    public abstract void send(byte[] data, int size) throws IOException;

    public abstract void disconnect();

    public abstract Protocol getProtocol();
}
