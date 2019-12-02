package net.marvk.marpletraffic.graph.edit;

import net.marvk.marpletraffic.graph.Graph;
import net.marvk.marpletraffic.graph.Road;
import net.marvk.marpletraffic.graph.RoadNode;
import org.locationtech.jts.geom.Coordinate;

import java.security.interfaces.RSAPrivateCrtKey;

public class RoadSplit implements GraphModification, RoadStartPoint {
    private final Road road;
    private final Coordinate location;
    private RoadNode roadNode;

    public RoadSplit(final Road road, final Coordinate location) {
        this.road = road;
        this.location = location;
    }

    public Road getRoad() {
        return road;
    }

    @Override
    public Coordinate getLocation() {
        return location;
    }

    @Override
    public void apply(final Graph graph) {
        roadNode = graph.splitRoad(this);
    }

    @Override
    public RoadNode getRoadNode() {
        if (roadNode == null) {
            throw new IllegalStateException("Must apply graph modification first");
        }

        return roadNode;
    }
}
