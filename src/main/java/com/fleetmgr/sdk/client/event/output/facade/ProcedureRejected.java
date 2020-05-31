package com.fleetmgr.sdk.client.event.output.facade;

import com.fleetmgr.interfaces.facade.control.Command;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by: Bartosz Nawrot
 * Date: 26.09.2018
 * Description:
 */
@Getter
@ToString
public class ProcedureRejected extends FacadeEvent {

    private final Command procedure;
    private final String message;

    public ProcedureRejected(Command procedure, String message) {
        super(FacadeEvent.Type.PROCEDURE_REJECTED);
        this.procedure = procedure;
        this.message = message;
    }
}
