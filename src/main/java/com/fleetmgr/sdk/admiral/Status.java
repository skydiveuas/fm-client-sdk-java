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

    public enum Severity {
        NOMINAL, WARNING, ERROR
    }

    private Severity type;
    private String message;
    private Integer lastCheckpoint;
}
