package com.fleetmgr.sdk.adapter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class EndpointEvent {

    @Getter
    private final long channelId;
}
