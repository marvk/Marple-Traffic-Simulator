package net.marvk.marpletraffic.graph.edit;

import net.marvk.marpletraffic.graph.RoadNode;
import org.locationtech.jts.geom.Coordinate;

@FunctionalInterface
public interface RoadStartPoint {
    RoadNode getRoadNode();

    default Coordinate getLocation() {
        return getRoadNode().getLocation();
    }
}
