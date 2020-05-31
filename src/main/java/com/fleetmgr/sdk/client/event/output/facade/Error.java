package com.fleetmgr.sdk.client.event.output.facade;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.09.2018
 * Description:
 */
@Getter
@ToString
public class Error extends FacadeEvent {

    private final Throwable throwable;

    public Error(Throwable throwable) {
        super(Type.ERROR);
        this.throwable = throwable;
    }
}
