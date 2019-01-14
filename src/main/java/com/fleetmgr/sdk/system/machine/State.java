package com.fleetmgr.sdk.system.machine;

/**
 * Created by: Bartosz Nawrot
 * Date: 21.10.2018
 * Description:
 */
public interface State<Event> {
    State<Event> start();
    State<Event> handleEvent(Event event);
}
