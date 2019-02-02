package com.fleetmgr.sdk.client.traffic.socket;

import com.fleetmgr.interfaces.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.11.2018
 * Description:
 */
public class TcpSocket extends Socket {

    private ExecutorService executor;
    private java.net.Socket socket;

    private AtomicBoolean closed;

    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public TcpSocket(ExecutorService executor) {
        this.executor = executor;
        this.closed = new AtomicBoolean(true);
    }

    @Override
    public void connect(String ip, int port) throws Exception {
        socket = connectImpl(ip, port);
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
        closed.set(false);
    }

    protected java.net.Socket connectImpl(String ip, int port) throws Exception {
        java.net.Socket s = new java.net.Socket();
        s.connect(new InetSocketAddress(ip, port), 3000);
        return s;
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
            closed.set(true);
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
        while (!closed.get()) {
            try {
                int r = inputStream.read(buffer, 0, 1);
                if (r > 0) {
                    int len = inputStream.available();
                    if (len > BUFFER_SIZE) len = BUFFER_SIZE - 1;
                    int dataSize = inputStream.read(buffer, 1, len) + 1;
                    listener.onReceived(buffer, dataSize);
                }

                else {
                    Thread.sleep(1);
                }

            } catch (IOException e) {
                if (!closed.get()) {
                    e.printStackTrace();
                }
                break;
            } catch (InterruptedException ignored) {
            }
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
