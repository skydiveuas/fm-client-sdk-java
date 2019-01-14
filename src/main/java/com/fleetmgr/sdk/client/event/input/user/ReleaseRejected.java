package com.fleetmgr.sdk.client.event.input.user;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
public class ReleaseRejected extends UserEvent {

    private String message;

    public ReleaseRejected(String message) {
        super(Type.RELEASE_REJECTED);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ReleaseRejected{" +
                "message='" + message + '\'' +
                '}';
    }
}
