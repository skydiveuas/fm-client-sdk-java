package com.fleetmgr.sdk.client.traffic.socket;

import com.fleetmgr.interfaces.Protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.11.2018
 * Description:
 */
public class UdpSocket extends Socket {

    private ExecutorService executor;

    private DatagramSocket socket;

    private InetAddress dstAddress;
    private int dstPort;

    public UdpSocket(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void connect(String ip, int port) throws IOException {
        dstAddress = InetAddress.getByName(ip);
        dstPort = port;
        socket = new DatagramSocket(0);
    }

    @Override
    public void startReading() {
        executor.execute(this::receptionThread);
    }

    @Override
    public int readBlocking(byte[] data) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);
        return packet.getLength();
    }

    @Override
    public void send(byte[] data, int size) throws IOException {
        socket.send(new DatagramPacket(data, size, dstAddress, dstPort));
    }

    @Override
    public void disconnect() {
        socket.close();
        listener.onClosed();
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.UDP;
    }

    private void receptionThread() {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, BUFFER_SIZE);
        while (!socket.isClosed()) {
            try {
                socket.receive(datagramPacket);
                listener.onReceived(
                        datagramPacket.getData(),
                        datagramPacket.getLength());
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    listener.getLogger().error("Reception problem", e);
                }
                break;
            }
        }
    }
}
