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
public class FacadeConfig {

    boolean useTls;
    private String certPath;

    public FacadeConfig validate() throws Exception {
        return validate(this);
    }

    public static FacadeConfig validate(FacadeConfig yaml) throws Exception {
        FacadeConfig result = new FacadeConfig();
        result.setUseTls(yaml.useTls);
        if (yaml.useTls && yaml.certPath == null) {
            throw new Exception("CertPath is mandatory when useTls == true");
        }
        result.setCertPath(yaml.certPath);
        return yaml;
    }

    public static FacadeConfig createDefault() {
        FacadeConfig result = new FacadeConfig();
        result.useTls = false;
        return result;
    }
}
