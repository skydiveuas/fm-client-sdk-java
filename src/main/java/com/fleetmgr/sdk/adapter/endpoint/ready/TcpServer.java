package com.fleetmgr.sdk.adapter.endpoint.ready;

import com.fleetmgr.sdk.adapter.Endpoint;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by: Bartosz Nawrot
 * Date: 17.07.2019
 * Description:
 */
public class TcpServer extends Endpoint {

    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

    private ServerSocket serverSocket;
    private ConcurrentLinkedDeque<ClientSocket> clients;
    private AtomicBoolean keepAccepting;

    @Override
    public void initialize(String input) throws Exception {
        JSONObject json = new JSONObject(input);

        serverSocket = new ServerSocket(json.getInt("port"));
        keepAccepting = new AtomicBoolean(true);
        clients = new ConcurrentLinkedDeque<>();
        getController().getClient().getExecutor().execute(this::acceptThread);

        logger.info("Initialized at: {}", serverSocket.getLocalSocketAddress());
    }

    @Override
    public void shutdown() {
        keepAccepting.set(false);
        try {
            serverSocket.close();
            for (ClientSocket s : clients) {
                s.shutdown();
            }
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Override
    public void handleData(byte[] data, int size) {
        for (ClientSocket s : clients) {
            try {
                s.send(data, size);
            } catch (IOException e) {
                logger.info("Client removed, size: " + clients.size());
                s.shutdown();
                clients.remove(s);
            }
        }
    }

    private void acceptThread() {
        while (keepAccepting.get()) {
            try {
                Socket s = serverSocket.accept();
                logger.info("New client connected: " + s.getInetAddress());
                clients.add(new ClientSocket(s));
            } catch (IOException e) {
                if (keepAccepting.get())
                    logger.error("", e);
            }
        }
    }

    private class ClientSocket {

        static final int BUFFER_SIZE = 0xFFF;

        private Socket socket;
        private InputStream input;
        private OutputStream output;
        private AtomicBoolean keepReception;

        ClientSocket(Socket socket) throws IOException {
            this.socket = socket;
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
            this.keepReception = new AtomicBoolean(true);
            getController().getClient().getExecutor().execute(this::receptionThread);
        }

        void shutdown() {
            keepReception.set(false);
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }

        void send(byte[] data, int size) throws IOException {
            output.write(data, 0, size);
        }

        private void receptionThread() {
            byte[] buffer = new byte[BUFFER_SIZE];
            while(keepReception.get()) {
                try {
                    int r = input.read(buffer, 0, 1);
                    if (r > 0) {
                        int len = input.available();
                        if (len > BUFFER_SIZE - 1) len = BUFFER_SIZE - 1;
                        int size = input.read(buffer, 1, len) + 1;
                        getController().send(buffer, size);
                    } else {
                        Thread.sleep(1);
                    }

                } catch (IOException e) {
                    if (keepReception.get()) {
                        logger.error("Reception problem", e);
                    }
                    break;
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
