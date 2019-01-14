package com.fleetmgr.sdk.client.traffic.socket;

import com.fleetmgr.interfaces.Protocol;

import java.io.IOException;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.11.2018
 * Description:
 */
public class TcpSocket extends Socket {

    @Override
    public void connect(String ip, int port) throws IOException {

    }

    @Override
    public void startReading() {

    }

    @Override
    public int readBlocking(byte[] data) throws IOException {
        return 0;
    }

    @Override
    public void send(byte[] data, int size) throws IOException {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public Protocol getProtocol() {
        return Protocol.TCP;
    }
}
