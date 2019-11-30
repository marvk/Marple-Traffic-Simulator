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
    private final double speed;
    private final List<Agent> otherAgents;
    private final String name;

    private Position current;
    private Position next;

    private Lane futureLane;

    public Agent(final Lane currentLane, final double speed, final List<Agent> otherAgents) {
        this(currentLane, speed, otherAgents, null);
    }

    public Agent(final Lane currentLane, final double speed, final List<Agent> otherAgents, final String name) {
        this.speed = speed;
        this.otherAgents = otherAgents;

        this.name = name;

        this.current = new Position(currentLane, 0);
        this.futureLane = randomFutureLane(currentLane);
    }

    public void calculateNext() {
        if (otherAgentsOnDifferentLaneWithSameOutNode(futureLane) && current.distanceFromStart >= current.lane.getLength() - MIN_DISTANCE) {
            next = current;
            return;
        }

        final OptionalDouble distanceOfAgentOnSameLane = distanceOfAgentOnSameLane();
        final OptionalDouble distanceFromStartOfAgentOnFutureLane = distanceFromStartOfAgentOnFutureLane();

        if (name != null) {
            System.out.println("name = " + name);
            System.out.println("distanceOfAgentOnSameLane = " + distanceOfAgentOnSameLane);
            System.out.println("distanceFromStartOfAgentOnFutureLane = " + distanceFromStartOfAgentOnFutureLane);
            System.out.println();
        }

        final double distanceOfAgentAhead = distanceOfAgentAhead(distanceOfAgentOnSameLane, distanceFromStartOfAgentOnFutureLane);

        final double currentSpeed = currentSpeed(distanceOfAgentAhead);

        double nextDistanceFromStart = Math.max(Math.min(current.distanceFromStart + currentSpeed, current.distanceFromStart + distanceOfAgentAhead - MIN_DISTANCE), current.distanceFromStart);

        Lane nextLane = current.lane;

        while (nextDistanceFromStart > nextLane.getLength()) {
            nextDistanceFromStart -= current.lane.getLength();

            nextLane = futureLane;
            futureLane = randomFutureLane(nextLane);

            final Lane finalNextLane = nextLane;

            if (otherAgentsOnDifferentLaneWithSameOutNode(finalNextLane)) {
                futureLane = nextLane;
                next = new Position(current.lane, current.lane.getLength());
                return;
            }
        }

        next = new Position(nextLane, nextDistanceFromStart);
    }

    private double currentSpeed(final double distanceOfAgentAhead) {
        if (distanceOfAgentAhead < SLOW_DISTANCE) {
            return speed * ((distanceOfAgentAhead - MIN_DISTANCE) / (SLOW_DISTANCE - MIN_DISTANCE));
        } else {
            return speed;
        }
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

    private double distanceOfAgentAhead(final OptionalDouble distanceOfAgentOnSameLane, final OptionalDouble distanceFromStartOfAgentOnFutureLane) {
        final double distanceOfAgentAhead;
        if (distanceOfAgentOnSameLane.isPresent()) {
            distanceOfAgentAhead = distanceOfAgentOnSameLane.getAsDouble();
        } else if (distanceFromStartOfAgentOnFutureLane.isPresent()) {
            distanceOfAgentAhead = (current.lane.getLength() - current.distanceFromStart) + distanceFromStartOfAgentOnFutureLane
                    .getAsDouble();
        } else {
            distanceOfAgentAhead = Double.MAX_VALUE;
        }
        return distanceOfAgentAhead;
    }

    private boolean otherAgentsOnDifferentLaneWithSameOutNode(final Lane finalNextLane) {
        return otherAgents.stream()
                          .map(Agent::getLane)
                          .filter(e -> !e.equals(finalNextLane))
                          .anyMatch(e -> e.getTo().equals(finalNextLane.getTo()));
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

    private static class Position {
        private final Lane lane;
        private final Coordinate location;
        private final double distanceFromStart;

        public Position(final Lane lane, final double distanceFromStart) {
            this.lane = lane;
            this.location = lane.getLocation(distanceFromStart);
            this.distanceFromStart = distanceFromStart;
        }
    }
}
