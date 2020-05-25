package com.fleetmgr.adapter.api;

import com.fleetmgr.adapter.Adapter;
import com.fleetmgr.adapter.configuration.ApiConfig;
import com.fleetmgr.interfaces.Location;
import com.fleetmgr.sdk.client.event.input.user.RequestControl;
import com.fleetmgr.sdk.client.event.input.user.UserEvent;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 11.09.2019
 * Description:
 */
public class Service {

    private static Logger logger = LoggerFactory.getLogger(Service.class);

    private Adapter adapter;

    public Service(Adapter adapter) {
        this.adapter = adapter;
    }

    public void initialize(ExecutorService executor) throws IOException {
        ApiConfig configuration = adapter.getAdapterConfig().getApi();
        InetSocketAddress address = new InetSocketAddress(
                configuration.getHost(),
                configuration.getPort());

        HttpServer server = HttpServer.create(address, 0);
        server.createContext("/events", new EventsHandler());
        server.createContext("/locations", new LocationsHandler());
        server.setExecutor(executor);
        server.start();

        logger.info("Initialized at: {}", server.getAddress());
    }

    class EventsHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            logger.info("Handling {} {}", t.getRequestMethod(), t.getRequestURI());

            if (!t.getRequestMethod().equals("POST")) {
                JSONObject response = new JSONObject();
                response.put("message", "Unexpected method: " + t.getRequestMethod());
                respond(t, HttpURLConnection.HTTP_BAD_METHOD, response.toString());
            }

            try {
                UserEvent event = parseEvent(getBody(t));
                JSONObject response = new JSONObject();
                response.put("event", event.toString());
                respond(t, HttpURLConnection.HTTP_CREATED, response.toString());
                adapter.getClient().notifyEvent(event);

            } catch (Exception e) {
                JSONObject response = new JSONObject();
                response.put("message", e.getMessage());
                respond(t, HttpURLConnection.HTTP_BAD_REQUEST, response.toString());
            }
        }

        private UserEvent parseEvent(String body) throws Exception {
            JSONObject json = new JSONObject(body);
            UserEvent.Type type = UserEvent.Type.valueOf(json.getString("type"));
            switch (type) {
                case RELEASE:
                    return new UserEvent(type);

                case REQUEST_CONTROL:
                    return new RequestControl(json.getInt("channelId"));

                default:
                    throw new Exception("Unexpected event type: " + type);
            }
        }
    }

    class LocationsHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            logger.info("Handling {} {}", t.getRequestMethod(), t.getRequestURI());

            if (!t.getRequestMethod().equals("POST")) {
                JSONObject response = new JSONObject();
                response.put("message", "Unexpected method: " + t.getRequestMethod());
                respond(t, HttpURLConnection.HTTP_BAD_METHOD, response.toString());
            }

            try {
                Location location = parseLocation(getBody(t));
                JSONObject response = new JSONObject();
                response.put("location", location.toString());
                respond(t, HttpURLConnection.HTTP_CREATED, response.toString());
                adapter.setLocation(location);

            } catch (Exception e) {
                JSONObject response = new JSONObject();
                response.put("message", e.getMessage());
                respond(t, HttpURLConnection.HTTP_BAD_REQUEST, response.toString());
            }
        }

        private Location parseLocation(String body) throws Exception {
            JSONObject json = new JSONObject(body);
            return Location.newBuilder()
                    .setLatitude(json.getDouble("lat"))
                    .setLongitude(json.getDouble("lng"))
                    .setAltitude(json.getDouble("alt"))
                    .build();
        }
    }

    private String getBody(HttpExchange t) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(t.getRequestBody()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    private void respond(HttpExchange t, int code, String body) throws IOException {
        logger.info("Responding {} {} with {}, body: {}",
                t.getRequestMethod(), t.getRequestURI(), code, body);
        Headers h = t.getResponseHeaders();
        h.add("Content-Type", "application/json");
        t.sendResponseHeaders(code, body.length());
        OutputStream os = t.getResponseBody();
        os.write(body.getBytes());
        os.close();
    }
}
