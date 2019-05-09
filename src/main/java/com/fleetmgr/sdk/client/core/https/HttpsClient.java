package com.fleetmgr.sdk.client.core.https;

import com.google.api.HttpRule;
import org.cfg4j.provider.ConfigurationProvider;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Level;

import static com.google.api.HttpRule.PatternCase.GET;

/**
 * Created by: Bartosz Nawrot
 * Date: 07.11.2018
 * Description:
 */
public class HttpsClient {

    public interface Listener {
        void log(Level level, String message);
    }

    private final String address;
    private final String apiKey;
    private final Listener listener;

    public HttpsClient(ConfigurationProvider configuration, Listener listener) {
        String coreHost = configuration.getProperty("core.host", String.class);
        String corePort = configuration.getProperty("core.port", String.class);
        this.address = "https://" + coreHost + ":" + corePort;
        this.apiKey = configuration.getProperty("apiKey", String.class);
        this.listener = listener;
    }

    public String execute(String path, HttpRule.PatternCase method) throws IOException {
        return execute(path, method, null);
    }

    public String execute(String path, HttpRule.PatternCase method, String body) throws IOException {
        URL url = new URL(address + path);
        listener.log(Level.INFO, "Execute " + method.name() + ": " + url.toString() + " body: " + body);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", apiKey);
        con.setRequestMethod(method.name());

        if (method != GET && body != null) {
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(body);
            wr.flush();
            wr.close();
        }

        con.setConnectTimeout(8000);
        con.setReadTimeout(8000);

        int result = con.getResponseCode();
        if (result >= HttpURLConnection.HTTP_OK && result < HttpURLConnection.HTTP_MULT_CHOICE) {
            String response = readResponse(con.getInputStream());
            listener.log(Level.INFO, "Response " + result + ": " + response);
            return response;

        } else {
            String cause = "Request failed with code: " + result;
            if (result >= HttpURLConnection.HTTP_BAD_REQUEST) {
                cause += " " + readResponse(con.getErrorStream());
            }
            throw new IOException(cause);
        }
    }

    private static String readResponse(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(is));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    static {
        // TODO remove this after tests!
        disableSslCertVerification();
    }

    private static void disableSslCertVerification() {
        try
        {
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

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
