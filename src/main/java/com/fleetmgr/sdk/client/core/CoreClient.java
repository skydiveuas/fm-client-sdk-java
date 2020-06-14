package com.fleetmgr.sdk.client.core;

import com.fleetmgr.sdk.client.core.http.HttpClient;
import com.fleetmgr.sdk.client.core.model.FacadeResponse;
import com.fleetmgr.sdk.client.core.model.OperateRequest;
import org.cfg4j.provider.ConfigurationProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Bartosz Nawrot
 * Date: 23.09.2018
 * Description:
 */
public class CoreClient {

    private final HttpClient client;
    
    private final String address;
    private final String apiKey;

    public CoreClient(ConfigurationProvider configuration) {
        this.client = new HttpClient();
        this.address = configuration.getProperty("core.address", String.class);
        this.apiKey = configuration.getProperty("core.apiKey", String.class);
    }

    public FacadeResponse attach() throws Exception {
        HttpClient.Call post = HttpClient.Call.builder()
                .address(address)
                .authorization(apiKey)
                .path("/devices/sessions")
                .method("POST")
                .body(new JSONObject())
                .build();
        return FacadeResponse.parse(client.execute(post, JSONObject.class));
    }

    public FacadeResponse operate(OperateRequest request) throws Exception {
        HttpClient.Call post = HttpClient.Call.builder()
                .address(address)
                .authorization(apiKey)
                .path("/controllers/sessions")
                .method("POST")
                .body(request.toJson())
                .build();
        return FacadeResponse.parse(client.execute(post, JSONObject.class));
    }

    public List<String> listDevices() throws Exception {
        HttpClient.Call get = HttpClient.Call.builder()
                .address(address)
                .authorization(apiKey)
                .path("/devices")
                .method("GET")
                .build();
        JSONArray response = client.execute(get, JSONArray.class);
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            result.add(response.getJSONObject(i).getString("id"));
        }
        return result;
    }

    public List<String> listConnectedDevices() throws Exception {
        HttpClient.Call get = HttpClient.Call.builder()
                .address(address)
                .authorization(apiKey)
                .path("/devices/sessions")
                .method("GET")
                .build();
        JSONArray response = client.execute(get, JSONArray.class);
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            result.add(response.getJSONObject(i).getString("sessionId"));
        }
        return result;
    }
}
