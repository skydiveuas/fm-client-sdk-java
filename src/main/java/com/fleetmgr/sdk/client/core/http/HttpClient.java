package com.fleetmgr.sdk.client.core.http;

import com.google.api.HttpRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.google.api.HttpRule.PatternCase.GET;

/**
 * Created by: Bartosz Nawrot
 * Date: 07.11.2018
 * Description:
 */
public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private final String address;
    private final String apiKey;

    public HttpClient(String host, int port, String apiKey) {
        this("http://" + host + ":" + port, apiKey);
    }

    public HttpClient(String address, String apiKey) {
        this.address = address;
        this.apiKey = apiKey;
    }

    public String execute(String path, HttpRule.PatternCase method) throws IOException {
        return execute(path, method, null);
    }

    public String execute(String path, HttpRule.PatternCase method, String body) throws IOException {
        URL url = new URL(address + path);
        logger.info("Execute {}: {} body: {}", method.name(), url.toString(), body);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
            logger.info("Response {}: {}", result, response);
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
}
