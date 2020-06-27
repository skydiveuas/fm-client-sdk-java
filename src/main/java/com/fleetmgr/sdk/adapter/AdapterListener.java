package com.fleetmgr.sdk.adapter;

import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;

public interface AdapterListener {

    void onEvent(FacadeEvent event);
}
