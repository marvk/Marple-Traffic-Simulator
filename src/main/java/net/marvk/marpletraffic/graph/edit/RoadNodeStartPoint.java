package net.marvk.marpletraffic.graph.edit;

import net.marvk.marpletraffic.graph.RoadNode;

public class RoadNodeStartPoint implements RoadStartPoint {
    private final RoadNode roadNode;

    public RoadNodeStartPoint(final RoadNode roadNode) {
        this.roadNode = roadNode;
    }

    @Override
    public RoadNode getRoadNode() {
        return roadNode;
    }
}
