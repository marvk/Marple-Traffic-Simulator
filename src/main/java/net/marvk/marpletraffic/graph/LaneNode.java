package net.marvk.marpletraffic.graph;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class LaneNode {
    private final Coordinate location;
    private final Set<Lane> incomingLanes;
    private final Set<Lane> outgoingLanes;

    public LaneNode(final Coordinate location) {
        this.location = location;
        this.incomingLanes = new LinkedHashSet<>();
        this.outgoingLanes = new LinkedHashSet<>();
    }

    public void addLane(final Lane lane) {
        if (laneType(lane) == LaneType.INCOMING) {
            incomingLanes.add(lane);
        } else {
            outgoingLanes.add(lane);
        }
    }

    public void removeLane(final Lane lane) {
        if (laneType(lane) == LaneType.INCOMING) {
            incomingLanes.remove(lane);
        } else {
            outgoingLanes.remove(lane);
        }
    }

    public Coordinate getLocation() {
        return location;
    }

    public Set<Lane> getIncomingLanes() {
        return Collections.unmodifiableSet(incomingLanes);
    }

    public Set<Lane> getOutgoingLanes() {
        return Collections.unmodifiableSet(outgoingLanes);
    }

    @SuppressWarnings("ObjectEquality")
    private LaneType laneType(final Lane lane) {
        if (lane.getTo() == this) {
            return LaneType.INCOMING;
        } else if (lane.getFrom() == this) {
            return LaneType.OUTGOING;
        } else {
            throw new IllegalArgumentException("Lane " + lane + " does neither start nor end in this node");
        }
    }

    private enum LaneType {
        INCOMING,
        OUTGOING
    }
}
