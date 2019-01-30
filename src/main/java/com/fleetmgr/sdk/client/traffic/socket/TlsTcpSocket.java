package com.fleetmgr.sdk.client.traffic.socket;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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
    }

    @Override
    public java.net.Socket connectImpl(String ip, int port) throws Exception {
        SSLSocket socket = (SSLSocket)factory.createSocket(ip, port);
        socket.startHandshake();
        return socket;
    }

    static {
        // TODO remove it after tests
        disableTslCertVerification();
    }

    private static void disableTslCertVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            factory = sslContext.getSocketFactory();

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
}

