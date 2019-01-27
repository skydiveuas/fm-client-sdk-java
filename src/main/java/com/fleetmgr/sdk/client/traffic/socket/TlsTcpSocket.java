package com.fleetmgr.sdk.client.traffic.socket;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.11.2018
 * Description:
 */
public class TlsTcpSocket extends TcpSocket {

    public TlsTcpSocket(ExecutorService executor) {
        super(executor);
    }

    @Override
    public void connect(String ip, int port) throws IOException {
        super.connect(ip, port);
    }
}
