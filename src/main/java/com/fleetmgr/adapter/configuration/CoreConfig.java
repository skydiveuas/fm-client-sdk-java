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
