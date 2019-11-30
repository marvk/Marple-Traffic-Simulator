package net.marvk.marpletraffic.graph;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

public class Lane {
    private final LaneNode from;
    private final LaneNode to;

    private final double length;
    private final Vector2D fromToVector;

    public Lane(final LaneNode from, final LaneNode to) {
        this.from = from;
        this.to = to;

        this.from.addLane(this);
        this.to.addLane(this);

        this.length = new Vector2D(from.getLocation(), to.getLocation()).length();

        this.fromToVector = new Vector2D(from.getLocation(), to.getLocation());
    }

    public LaneNode getFrom() {
        return from;
    }

    public LaneNode getTo() {
        return to;
    }

    public double getLength() {
        return length;
    }

    public Coordinate getLocation(final double distanceFromStart) {
        if (distanceFromStart > length) {
            throw new IllegalArgumentException();
        }

        return fromToVector.multiply(distanceFromStart / length).translate(from.getLocation());
    }
}
