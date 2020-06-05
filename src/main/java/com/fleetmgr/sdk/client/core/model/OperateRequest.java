package com.fleetmgr.sdk.client.core.model;

import lombok.*;
import org.json.JSONObject;

/**
 * Created by: Bartosz Nawrot
 * Date: 14.05.2020
 * Description:
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OperateRequest {

    private String deviceId;
    private String serialId;

    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        result.put("deviceId", deviceId);
        result.put("serialId", serialId);
        return result;
    }
}
