package com.fleetmgr.adapter.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by: Bartosz Nawrot
 * Date: 17.07.2019
 * Description:
 */
@ToString
@Getter
@Setter
public class FilterConfig {

    String object;
    String input;

    public FilterConfig validate() {
        return validate(this);
    }

    public static FilterConfig validate(FilterConfig yaml) {
        // TODO implement validation
        return yaml;
    }
}
