package com.fleetmgr.sdk.client.event.output.facade;

/**
 * Created by: Bartosz Nawrot
 * Date: 18.09.2018
 * Description:
 */
public class FacadeEvent {

    public enum Type {
        ERROR,
        ATTACHED,
        RELEASED,
        OPERATION_STARTED,
        OPERATION_UPDATED,
        CHANNELS_OPENED,
        HANDOVER_ACCEPTED,
        REQUEST_CONTROL,
        RELEASE_CONTROL,
        PROCEDURE_REJECTED,
        HANDOVER_DONE,
        CONTROL_RELEASED,
        CHANNELS_CLOSING,
        OPERATION_ENDED,
        UNREACHABLE,
        CONNECTION_RECOVERED,
    }

    private Type type;

    public FacadeEvent(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "FacadeEvent{" +
                "type=" + type +
                '}';
    }
}
