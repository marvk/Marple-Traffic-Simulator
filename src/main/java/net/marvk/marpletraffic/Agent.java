package net.marvk.marpletraffic;

import net.marvk.marpletraffic.graph.Lane;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Agent {
    private static final int SLOW_DISTANCE = 100;
    private static final int MIN_DISTANCE = 20;
    private final double maxSpeed;
    private final List<Agent> otherAgents;
    private final String name;

    private Position current;
    private Position next;

    private Lane futureLane;

    public Agent(final Lane currentLane, final double maxSpeed, final List<Agent> otherAgents) {
        this(currentLane, maxSpeed, otherAgents, null);
    }

    public Agent(final Lane currentLane, final double maxSpeed, final List<Agent> otherAgents, final String name) {
        this.maxSpeed = maxSpeed;
        this.otherAgents = otherAgents;

        this.name = name;

        this.current = new Position(currentLane, 0, 0);
        this.futureLane = randomFutureLane(currentLane);
    }

    public void calculateNext() {
        final OptionalDouble nextLaneIntersectionOccupiedStopPoint;

        if (areOtherAgentsOnDifferentLaneWithSameOutNode(futureLane)) {
            nextLaneIntersectionOccupiedStopPoint = OptionalDouble.of(current.lane.getLength());
        } else {
            nextLaneIntersectionOccupiedStopPoint = OptionalDouble.empty();
        }

        final OptionalDouble distanceOfAgentOnSameLane = distanceOfAgentOnSameLane();
        final OptionalDouble distanceFromStartOfAgentOnFutureLane = distanceFromStartOfAgentOnFutureLane();

//        if (name != null) {
//            System.out.println("name = " + name);
////            System.out.println("distanceOfAgentOnSameLane = " + distanceOfAgentOnSameLane);
////            System.out.println("distanceFromStartOfAgentOnFutureLane = " + distanceFromStartOfAgentOnFutureLane);
//            System.out.println(current);
//        }

        final double stopDistanceAhead = stopPoint(distanceOfAgentOnSameLane, distanceFromStartOfAgentOnFutureLane, nextLaneIntersectionOccupiedStopPoint);

        final double currentMaxSpeed = Math.max(Math.min((current.speed + 0.05) * 1.05, maxSpeed), 0);

        final double currentSpeed = currentSpeed(stopDistanceAhead, currentMaxSpeed);

        double nextDistanceFromStart = Math.max(Math.min(current.distanceFromStart + currentSpeed, current.distanceFromStart + stopDistanceAhead - MIN_DISTANCE), current.distanceFromStart);

        final double actualSpeed = nextDistanceFromStart - current.distanceFromStart;

        Lane nextLane = current.lane;

        while (nextDistanceFromStart > nextLane.getLength()) {
            nextDistanceFromStart -= current.lane.getLength();

            nextLane = futureLane;
            futureLane = randomFutureLane(nextLane);
        }

        next = new Position(nextLane, nextDistanceFromStart, actualSpeed);
    }

    private double currentSpeed(final double distanceOfAgentAhead, final double maxSpeed) {
        final double result;

        if (distanceOfAgentAhead < SLOW_DISTANCE) {
            result = this.maxSpeed * ((distanceOfAgentAhead - MIN_DISTANCE) / (SLOW_DISTANCE - MIN_DISTANCE));
        } else {
            result = this.maxSpeed;
        }

        return Math.min(maxSpeed, result);
    }

    private OptionalDouble distanceOfAgentOnSameLane() {
        return otherAgents.stream()
                          .filter(a -> a.getLane().equals(current.lane))
                          .filter(e1 -> e1.getDistanceFromStart() > current.distanceFromStart)
                          .mapToDouble(Agent::getDistanceFromStart)
                          .map(d -> d - current.distanceFromStart)
                          .min();
    }

    private OptionalDouble distanceFromStartOfAgentOnFutureLane() {
        return otherAgents.stream()
                          .filter(a -> a.getLane().equals(futureLane))
                          .mapToDouble(Agent::getDistanceFromStart)
                          .min();
    }

    private double stopPoint(final OptionalDouble distanceOfAgentOnSameLane, final OptionalDouble distanceFromStartOfAgentOnFutureLane, final OptionalDouble nextLaneIntersectionOccupiedStopPoint) {
        if (distanceOfAgentOnSameLane.isPresent()) {
            return distanceOfAgentOnSameLane.getAsDouble();
        } else if (distanceFromStartOfAgentOnFutureLane.isPresent()) {
            return (current.lane.getLength() - current.distanceFromStart) + distanceFromStartOfAgentOnFutureLane
                    .getAsDouble();
        } else if (nextLaneIntersectionOccupiedStopPoint.isPresent()) {
            return nextLaneIntersectionOccupiedStopPoint.getAsDouble() - current.distanceFromStart;
        } else {
            return Double.MAX_VALUE;
        }
    }

    private boolean areOtherAgentsOnDifferentLaneWithSameOutNode(final Lane nextLane) {
        return otherAgents.stream()
                          .map(Agent::getLane)
                          .filter(e -> !e.equals(nextLane))
                          .anyMatch(e -> e.getTo().equals(nextLane.getTo()));
    }

    public void step() {
        current = next;
        next = null;
    }

    private static Lane randomFutureLane(final Lane nextLane) {
        final Set<Lane> outgoingLanes = nextLane.getTo().getOutgoingLanes();
        final int n = ThreadLocalRandom.current().nextInt(outgoingLanes.size());

        return outgoingLanes.stream()
                            .skip(n)
                            .findFirst()
                            .orElseThrow();
    }

    public Coordinate getLocation() {
        return current.location;
    }

    public Lane getLane() {
        return current.lane;
    }

    public double getDistanceFromStart() {
        return current.distanceFromStart;
    }

    public String getName() {
        return name;
    }

    private static class Position {
        private final Lane lane;
        private final Coordinate location;
        private final double distanceFromStart;
        private final double speed;

        private Position(final Lane lane, final double distanceFromStart, final double speed) {
            this.lane = lane;
            this.location = lane.getLocation(distanceFromStart);
            this.distanceFromStart = distanceFromStart;
            this.speed = speed;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "lane=" + lane +
                    ", location=" + location +
                    ", distanceFromStart=" + distanceFromStart +
                    ", speed=" + speed +
                    '}';
        }
    }
}
