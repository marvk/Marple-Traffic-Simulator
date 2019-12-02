package net.marvk.marpletraffic.graph.edit;

import net.marvk.marpletraffic.graph.Road;
import org.locationtech.jts.geom.Coordinate;

public class RoadSplit {
    private final Road road;
    private final Coordinate location;

    public RoadSplit(final Road road, final Coordinate location) {
        this.road = road;
        this.location = location;
    }

    public Road getRoad() {
        return road;
    }

    public Coordinate getLocation() {
        return location;
    }
}
