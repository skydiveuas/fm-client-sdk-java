package com.fleetmgr.sdk.client.core.model;

import lombok.Builder;
import lombok.ToString;
import org.json.JSONObject;

/**
 * Created by: Bartosz Nawrot
 * Date: 14.05.2020
 * Description:
 */
@Builder
@ToString
public class OperateRequest {

    private final String device;
    private final String serial;

    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        result.put("device", device);
        result.put("serial", serial);
        return result;
    }
}
