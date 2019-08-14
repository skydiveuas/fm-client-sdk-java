package com.fleetmgr.sdk.admiral;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Created by: Bartosz Nawrot
 * Date: 04.07.2019
 * Description:
 */
@Builder
@Getter
@ToString
public class Mission {

    private List<Waypoint> waypoints;
}
