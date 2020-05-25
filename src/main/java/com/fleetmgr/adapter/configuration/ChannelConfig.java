package com.fleetmgr.adapter.configuration;

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
public class ChannelConfig {

    public enum HoPolicy {
        ALWAYS,
        NEVER,
        IMPL_DEFINED
    }

    Long id;
    Protocol protocol;
    Security security;
    Priority priority;
    HoPolicy hoPolicy;
    EndpointConfig endpoint;
    FilterConfig filter;

    public ChannelConfig validate(Role role) throws Exception {
        return validate(this, role);
    }

    public static ChannelConfig validate(ChannelConfig yaml, Role role) throws Exception {
        ChannelConfig result = new ChannelConfig();
        if (yaml.id == null) {
            throw new Exception("ChannelConfig ID is mandatory");
        }
        result.setId(yaml.id);

        if (role == Role.PILOT) {
            if (yaml.protocol == null) {
                throw new Exception("ChannelConfig ID is mandatory");
            }
            result.setProtocol(yaml.protocol);

            if (yaml.security == null) {
                result.setSecurity(Security.PLAIN_TEXT);
            } else {
                result.setSecurity(yaml.security);
            }

            if (yaml.priority == null) {
                result.setPriority(Priority.BEST_EFFORT);
            } else {
                result.setPriority(yaml.priority);
            }

            if (yaml.hoPolicy == null) {
                result.setHoPolicy(HoPolicy.ALWAYS);
            } else {
                result.setHoPolicy(yaml.hoPolicy);
            }
        }

        if (yaml.endpoint != null) {
            result.setEndpoint(yaml.endpoint.validate());
        }

        if (yaml.filter != null) {
            result.setFilter(yaml.filter.validate());
        }

        return result;
    }
}
