package com.fleetmgr.sdk.adapter.configuration;

import lombok.*;

/**
 * Created by: Bartosz Nawrot
 * Date: 16.07.2019
 * Description:
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiConfig {

    String host;
    Integer port;

    public ApiConfig validate() {
        return validate(this);
    }

    public static ApiConfig validate(ApiConfig yaml) {
        // TODO implement validation
        return yaml;
    }
}
