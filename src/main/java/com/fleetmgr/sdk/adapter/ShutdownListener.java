package com.fleetmgr.sdk.adapter;

public interface ShutdownListener {

    void onRelease();

    void onError(Throwable throwable);
}
