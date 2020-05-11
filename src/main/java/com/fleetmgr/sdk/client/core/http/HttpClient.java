package com.fleetmgr.sdk.client.core.http;

import com.squareup.okhttp.*;
import lombok.Builder;
import lombok.ToString;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by: Bartosz Nawrot
 * Date: 09.05.2020
 * Description:
 */
public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private final OkHttpClient client = new OkHttpClient();

    public JSONObject execute(Call call) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(call.address + call.path)
                .addHeader("Authorization", call.authorization);

        if (call.body != null) {
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    call.body.toString());
            requestBuilder.method(call.method, requestBody);
        } else {
            requestBuilder.method(call.method, null);
        }

        Request request = requestBuilder.build();

        logger.info("Starting call: {}", call);
        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
        
        if (response.isSuccessful()) {
            logger.info("Call finished, status:{}, responseBody:{}",
                    response.code(), responseString);
            return new JSONObject(responseString);
        } else {
            logger.error("Call failed, status:{}, responseBody:{}",
                    response.code(), responseString);
            throw new IOException(response.toString());
        }
    }

    @Builder
    @ToString
    public static class Call {
        private final String address;
        private final String path;
        private final String authorization;
        private final String method;
        private final JSONObject body;
    }
}
