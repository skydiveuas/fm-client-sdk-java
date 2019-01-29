package com.fleetmgr.sdk.client.traffic.socket;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 27.01.2019
 * Description:
 */
public class TlsTcpSocket extends TcpSocket {

    private static SSLSocketFactory factory;

    public TlsTcpSocket(ExecutorService executor) {
        super(executor);
        factory = null;
    }

    @Override
    public java.net.Socket connectImpl(String ip, int port) throws Exception {
        if (factory == null) {
            // initialise the keystore
            char[] password = "password".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("core.jks");
            ks.load(fis, password);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            factory = sslContext.getSocketFactory();
        }

        SSLSocket socket = (SSLSocket)factory.createSocket(ip, port);

        socket.startHandshake();

        return socket;
    }
}

