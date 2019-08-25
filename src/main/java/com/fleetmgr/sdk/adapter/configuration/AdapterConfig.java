package com.fleetmgr.sdk.adapter.configuration;

import com.fleetmgr.interfaces.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by: Bartosz Nawrot
 * Date: 16.07.2019
 * Description:
 */
@ToString
@Getter
@Setter
public class AdapterConfig {

    private Role role;
    private Long deviceId;
    private LocationConfig location;
    private CoreConfig core;
    private FacadeConfig facade;
    private Map<String, ChannelConfig> channels;

    public AdapterConfig validate() throws Exception {
        return validate(this);
    }

    public static AdapterConfig validate(AdapterConfig yaml) throws Exception {
        AdapterConfig result = new AdapterConfig();
        if (yaml.role == null) {
            throw new Exception("Role is mandatory");
        }
        result.setRole(yaml.role);

        if (result.getRole() == Role.PILOT && yaml.deviceId == null) {
            throw new Exception("DeviceId is mandatory when Role == PILOT");
        }
        result.setDeviceId(yaml.deviceId);

        if (yaml.location != null) {
            result.setLocation(yaml.location.validate());
        }

        if (yaml.core == null) {
            throw new Exception("CoreConfig is mandatory");
        }
        result.setCore(yaml.core.validate());

        if (yaml.facade == null) {
            result.setFacade(FacadeConfig.createDefault());
        } else  {
            result.setFacade(yaml.facade.validate());
        }

        if (yaml.role == Role.PILOT && yaml.channels.size() < 1) {
            throw new Exception("At least one channel must be specified when Role == PILOT");
        }
        HashSet<Long> ids = new HashSet<>();
        HashMap<String, ChannelConfig> channels = new HashMap<>();
        for (Map.Entry<String, ChannelConfig> e : yaml.channels.entrySet()) {
            try {
                ChannelConfig validated = e.getValue().validate(result.role);
                if (ids.contains(validated.id)) {
                    throw new Exception("More than one ChannelConfig for the same Id");
                }
                ids.add(validated.id);
                channels.put(e.getKey(), validated);

            } catch (Exception ex) {
                throw new Exception("Validation failed for ChannelConfig " + e.getValue(), ex);
            }
        }
        result.setChannels(channels);

        return result;
    }
}
