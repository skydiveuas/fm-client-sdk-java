package com.fleetmgr.sdk.client.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

/**
 * Created by: Bartosz Nawrot
 * Date: 14.05.2020
 * Description:
 */
@Getter
@Builder
@ToString
public class FacadeResponse {

    private final String host;
    private final Integer unsafePort;
    private final Integer tlsPort;
    private final String key;

    public static FacadeResponse parse(JSONObject input) {
        return FacadeResponse.builder()
                .host(input.getString("host"))
                .unsafePort(input.getInt("unsafePort"))
                .tlsPort(input.getInt("tlsPort"))
                .key(input.getString("key"))
                .build();
    }
}
