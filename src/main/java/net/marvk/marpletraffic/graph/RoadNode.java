package net.marvk.marpletraffic.graph;

import org.locationtech.jts.geom.Coordinate;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class RoadNode {
    private final Coordinate location;
    private final Set<Road> roads;

    public RoadNode(final Coordinate location) {
        this.location = location;
        this.roads = new LinkedHashSet<>();
    }

    public Coordinate getLocation() {
        return location;
    }

    public Set<Road> getRoads() {
        return Collections.unmodifiableSet(roads);
    }

    public void addRoad(final Road road) {
        roads.add(road);
    }

    public int getMostLanesConnected() {
        return roads.stream().mapToInt(Road::getTotalNumberOfLanes).max().orElse(0);
    }

    public void removeRoad(final Road road) {
        roads.remove(road);
    }

    public boolean isConnected() {
        return !roads.isEmpty();
    }

    public double distance(final RoadNode roadNode) {
        return distance(roadNode.location);
    }

    public double distance(final Coordinate coordinate) {
        return location.distance(coordinate);
    }

    @Override
    public String toString() {
        return "RoadNode{" +
                "location=" + location +
                ", roads=" + roads +
                '}';
    }
}
