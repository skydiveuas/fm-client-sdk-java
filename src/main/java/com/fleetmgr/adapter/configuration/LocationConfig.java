package com.fleetmgr.adapter.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by: Bartosz Nawrot
 * Date: 16.07.2019
 * Description:
 */
@ToString
@Getter
@Setter
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
