package net.marvk.marpletraffic.graph;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

public class Lane {
    private final LaneNode from;
    private final LaneNode to;

    private final double length;
    private final Vector2D fromToVector;
    private final Vector2D directionNormal;

    public Lane(final LaneNode from, final LaneNode to) {
        this.from = from;
        this.to = to;

        this.from.addLane(this);
        this.to.addLane(this);

        this.length = new Vector2D(from.getLocation(), to.getLocation()).length();

        this.fromToVector = new Vector2D(from.getLocation(), to.getLocation());
        this.directionNormal = fromToVector.normalize();
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

    public Vector2D getDirectionNormal() {
        return directionNormal;
    }

    public Coordinate getLocation(final double distanceFromStart) {
        if (distanceFromStart > length) {
            throw new IllegalArgumentException();
        }

        return fromToVector.multiply(distanceFromStart / length).translate(from.getLocation());
    }

    public void unlink() {
        from.removeLane(this);
        to.removeLane(this);
    }

    @Override
    public String toString() {
        return "Lane{" +
                "from=" + from.getLocation() +
                ", to=" + to.getLocation() +
                ", length=" + length +
                ", fromToVector=" + fromToVector +
                ", directionNormal=" + directionNormal +
                '}';
    }
}
