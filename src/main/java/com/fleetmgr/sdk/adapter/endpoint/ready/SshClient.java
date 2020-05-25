package com.fleetmgr.sdk.adapter.endpoint.ready;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.07.2019
 * Description:
 */
public class SshClient extends TcpClient {

    private static final Logger logger = LoggerFactory.getLogger(SshClient.class);

    private AtomicBoolean connectionStarted;

    private byte[] buffer;
    private int bufferSize;

    @Override
    public void initialize(String input) throws Exception {
        connectionStarted = new AtomicBoolean(false);
        buffer = new byte[256];
        bufferSize = 0;
        JSONObject inputOverride = new JSONObject();
        inputOverride.put("host","localhost");
        inputOverride.put("port", 22);
        super.initialize(inputOverride.toString());
    }

    @Override
    public void handleData(byte[] data, int size) {
        connectionStarted.set(true);
        super.handleData(data, size);
    }

    @Override
    public void onReceived(byte[] data, int size) {
        if (connectionStarted.get()) {
            if (bufferSize != 0) {
                // include buffered data in the first transmitted packet
                byte[] temp = Arrays.copyOf(buffer, size + bufferSize);
                System.arraycopy(data, 0, temp, bufferSize, size);
                super.onReceived(temp, size + bufferSize);
                bufferSize = 0;

            } else {
                super.onReceived(data, size);
            }

        } else {
            bufferData(data, size);
        }
    }

    private void bufferData(byte[] data, int size) {
        logger.info("Buffering: " + size + " bytes, buffer size: " + bufferSize);
        if (bufferSize + size > buffer.length) {
            buffer = Arrays.copyOf(data, 2 * buffer.length);
        }
        System.arraycopy(data, 0, buffer, bufferSize, size);
        bufferSize += size;
    }
}
