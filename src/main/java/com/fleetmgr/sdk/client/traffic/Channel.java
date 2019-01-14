package com.fleetmgr.sdk.client.traffic;

import java.util.logging.Level;

import java.io.IOException;

/**
 * Created by: Bartosz Nawrot
 * Date: 19.12.2018
 * Description:
 */
public interface Channel {

    interface Listener {
        void onReceived(Channel channel, byte[] data, int size);
        void onClosed(Channel channel);
        void log(Level level, String message);
    }

    void setListener(Listener listener);

    void send(byte[] data, int size) throws IOException;

    long getId();

    boolean isOwned();
}
