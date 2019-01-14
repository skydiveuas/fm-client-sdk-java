package com.fleetmgr.sdk.client.event.output.facade;

import com.fleetmgr.interfaces.facade.control.Command;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.09.2018
 * Description:
 */
public class ProcedureRejected extends FacadeEvent {

    private Command procedure;
    private String message;

    public ProcedureRejected(Command procedure, String message) {
        super(FacadeEvent.Type.PROCEDURE_REJECTED);
        this.procedure = procedure;
        this.message = message;
    }

    public Command getProcedure() {
        return procedure;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ProcedureRejected{" +
                "procedure=" + procedure +
                ", message='" + message + '\'' +
                '}';
    }
}
