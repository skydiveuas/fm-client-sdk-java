package com.fleetmgr.sdk.client.traffic.socket;

import com.fleetmgr.interfaces.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.11.2018
 * Description:
 */
public class TcpSocket extends Socket {

    private ExecutorService executor;
    private java.net.Socket socket;

    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public TcpSocket(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void connect(String ip, int port) throws IOException {
        socket = new java.net.Socket();
        socket.connect(new InetSocketAddress(ip, port), 3000);
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void startReading() {
        executor.execute(this::receptionThread);
    }

    @Override
    public int readBlocking(byte[] data) throws IOException {
        return inputStream.read(data, 0, data.length);
    }

    @Override
    public void send(byte[] data, int size) throws IOException {
        outputStream.write(data, 0, size);
    }

    @Override
    public void disconnect() {
        try {
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        listener.onClosed();
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.TCP;
    }

    private void receptionThread() {
        byte[] buffer = new byte[BUFFER_SIZE];
        while (!socket.isClosed()) {
            try {
                int len = inputStream.available();
                if (len > BUFFER_SIZE) len = BUFFER_SIZE;

                int dataSize = inputStream.read(buffer, 0, len);

                if (dataSize > 0) {
                    listener.onReceived(buffer, dataSize);
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) { }

            } catch (IOException e) {
                if (!socket.isClosed()) {
                    e.printStackTrace();
                }
                break;
            }
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
