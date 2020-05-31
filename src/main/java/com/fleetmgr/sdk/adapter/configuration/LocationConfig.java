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
public class LocationConfig {

    Double lat;
    Double lon;
    Double alt;

    public LocationConfig validate() {
        return validate(this);
    }

    public static LocationConfig validate(LocationConfig yaml) {
        // TODO implement validation
        return yaml;
    }
}
