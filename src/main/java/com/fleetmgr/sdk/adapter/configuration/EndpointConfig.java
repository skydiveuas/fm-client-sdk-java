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
public class EndpointConfig {

    String object;
    String input;

    public EndpointConfig validate() {
        return validate(this);
    }

    public static EndpointConfig validate(EndpointConfig yaml) {
        // TODO implement validation
        return yaml;
    }
}
