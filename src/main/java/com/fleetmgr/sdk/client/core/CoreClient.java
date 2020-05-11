package com.fleetmgr.sdk.client.core;

import com.fleetmgr.interfaces.AttachResponse;
import com.fleetmgr.interfaces.ListDevicesResponse;
import com.fleetmgr.interfaces.OperateRequest;
import com.fleetmgr.interfaces.OperateResponse;
import com.fleetmgr.sdk.client.core.http.HttpClient;
import com.google.protobuf.util.JsonFormat;
import org.cfg4j.provider.ConfigurationProvider;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public AttachResponse attach() throws Exception {
        HttpClient.Call post = HttpClient.Call.builder()
                .address(address)
                .authorization(apiKey)
                .path("/devices/sessions")
                .method("POST")
                .body(new JSONObject())
                .build();
        JSONObject response = client.execute(post, JSONObject.class);
        AttachResponse.Builder builder = AttachResponse.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(response.toString(), builder);
        return builder.build();
    }

    public OperateResponse operate(OperateRequest request) throws Exception {
        HttpClient.Call post = HttpClient.Call.builder()
                .address(address)
                .authorization(apiKey)
                .path("/controllers/sessions")
                .method("POST")
                .body(new JSONObject(JsonFormat.printer().print(request)))
                .build();
        JSONObject response = client.execute(post, JSONObject.class);
        OperateResponse.Builder builder = OperateResponse.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(response.toString(), builder);
        return builder.build();
    }

    public ListDevicesResponse listDevices() throws Exception {
        HttpClient.Call get = HttpClient.Call.builder()
                .address(address)
                .authorization(apiKey)
                .path("/devices")
                .method("GET")
                .build();
        JSONArray response = client.execute(get, JSONArray.class);
        JSONObject responseJson = new JSONObject();
        responseJson.put("devices", response);
        ListDevicesResponse.Builder builder = ListDevicesResponse.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(responseJson.toString(), builder);
        return builder.build();
    }
}
