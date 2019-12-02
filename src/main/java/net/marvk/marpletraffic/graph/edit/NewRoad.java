package net.marvk.marpletraffic.graph.edit;

import net.marvk.marpletraffic.graph.Graph;
import net.marvk.marpletraffic.graph.Road;

import java.util.ArrayList;
import java.util.List;

public class NewRoad implements GraphModification {
    private final List<RoadStartPoint> roadStartPoints;

    public NewRoad() {
        this.roadStartPoints = new ArrayList<>(2);
    }

    public void add(final RoadStartPoint roadStartPoint) {
        if (roadStartPoints.size() < 2) {
            roadStartPoints.add(roadStartPoint);
        } else {
            throw new IndexOutOfBoundsException("NewRoad may only have two RoadStartPoints");
        }
    }

    public void removeLatest() {
        if (!roadStartPoints.isEmpty()) {
            roadStartPoints.remove(roadStartPoints.size() - 1);
        }
    }

    public int numberOfPoints() {
        return roadStartPoints.size();
    }

    public boolean isApplicable() {
        return numberOfPoints() == 2;
    }

    public RoadStartPoint getRoadStartPoint(final int index) {
        return roadStartPoints.get(index);
    }

    @Override
    public void apply(final Graph graph) {
        if (isApplicable()) {
            for (final RoadStartPoint roadStartPoint : roadStartPoints) {
                apply(roadStartPoint, graph);
            }
        } else {
            throw new IllegalStateException("Can not apply if there are less than two RoadStartPoints");
        }

        final Road newRoad = new Road(roadStartPoints.get(0).getRoadNode(), roadStartPoints.get(1).getRoadNode(), 1, 1);

        graph.addRoad(newRoad);
    }

    private static void apply(final RoadStartPoint startPoint, final Graph graph) {
        if (startPoint instanceof GraphModification) {
            ((GraphModification) startPoint).apply(graph);
        }
    }
}
