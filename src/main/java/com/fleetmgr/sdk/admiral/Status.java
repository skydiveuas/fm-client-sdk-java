package com.fleetmgr.sdk.admiral;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by: Bartosz Nawrot
 * Date: 05.07.2019
 * Description:
 */
@Builder
@Getter
@ToString
public class Status {

    public enum Type {
        NOMINAL, WARNING, ERROR
    }

    private Type type;
    private String message;
    private int lastWaypointId;
}
