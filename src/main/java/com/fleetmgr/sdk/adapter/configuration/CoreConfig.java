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
public class CoreConfig {

    String address;
    String apiKey;

    public CoreConfig validate() {
        return validate(this);
    }

    public static CoreConfig validate(CoreConfig yaml) {
        // TODO implement validation
        return yaml;
    }
}
