package com.fleetmgr.sdk.adapter.endpoint.ready;

import com.fleetmgr.sdk.adapter.Endpoint;
import com.fleetmgr.sdk.client.traffic.socket.Socket;
import com.fleetmgr.sdk.client.traffic.socket.UdpSocket;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by: Bartosz Nawrot
 * Date: 17.07.2019
 * Description:
 */
public class UdpEndpoint extends Endpoint implements
        Socket.Listener {

    private static final Logger logger = LoggerFactory.getLogger(UdpEndpoint.class);

    private UdpSocket socket;

    @Override
    public void initialize(String input) throws IOException {
        JSONObject json = new JSONObject(input);

        socket = new UdpSocket(getController().getClient().getExecutor());
        socket.setListener(this);
        socket.connect(json.getString("host"), json.getInt("port"));
        socket.startReading();
    }

    @Override
    public void shutdown() {
        socket.disconnect();
    }

    @Override
    public void handleData(byte[] data, int size) {
        try {
            socket.send(data, size);
        } catch (IOException e) {
            logger.error("Could not send data", e);
        }
    }

    @Override
    public void onReceived(byte[] data, int size) {
        getController().send(data, size);
    }

    @Override
    public void onClosed() {
        logger.info("Closed");
    }
}
