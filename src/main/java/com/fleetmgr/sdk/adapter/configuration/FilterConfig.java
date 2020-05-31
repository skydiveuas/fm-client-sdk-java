package com.fleetmgr.sdk.adapter.configuration;

import lombok.*;

/**
 * Created by: Bartosz Nawrot
 * Date: 17.07.2019
 * Description:
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
