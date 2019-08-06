package com.fleetmgr.sdk.adapter;

import com.fleetmgr.interfaces.Location;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by: Bartosz Nawrot
 * Date: 16.07.2019
 * Description:
 */
public abstract class Filter {

    public interface Listener {
        void onLocation(Location location);
    }

    @Setter
    @Getter
    private Listener listener;

    public abstract void initialize(String input);

    public abstract void shutdown();

    public abstract void onReceived(byte[] data, int size);

    public abstract void onSent(byte[] data, int size);
}
