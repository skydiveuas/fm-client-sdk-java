package com.fleetmgr.sdk.adapter.configuration;

import com.fleetmgr.interfaces.Priority;
import com.fleetmgr.interfaces.Protocol;
import com.fleetmgr.interfaces.Role;
import com.fleetmgr.interfaces.Security;
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
