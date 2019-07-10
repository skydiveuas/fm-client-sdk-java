package com.fleetmgr.sdk.client.event.output.facade;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.09.2018
 * Description:
 */
public class Error extends FacadeEvent {

    private Throwable throwable;

    public Error(Throwable throwable) {
        super(Type.ERROR);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return "Error{" +
                "message='" + throwable.getMessage() + '\'' +
                '}';
    }
}
