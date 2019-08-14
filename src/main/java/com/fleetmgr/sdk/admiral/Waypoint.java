package com.fleetmgr.sdk.admiral;

import com.fleetmgr.interfaces.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by: Bartosz Nawrot
 * Date: 13.08.2019
 * Description:
 */
@Builder
@Getter
@ToString
public class Waypoint {

    private double lat, lng, alt;
    private double velocity;

    public Location getLocation() {
        return Location.newBuilder()
                .setLatitude(lat)
                .setLongitude(lng)
                .setAltitude(alt)
                .build();
    }
}
