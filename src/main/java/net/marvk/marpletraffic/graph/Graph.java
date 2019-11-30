package net.marvk.marpletraffic.graph;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Graph {
    private final Set<RoadNode> roadNodes;
    private final Set<RoadNode> unmodifiableRoadNodes;

    private final Set<Road> roads;
    private final Set<Road> unmodifiableRoads;

    private final Set<Lane> lanes;
    private final Set<Lane> unmodifiableLanes;

    private final Set<LaneNode> laneNodes;
    private final Set<LaneNode> unmodifiableLaneNodes;
    private final double laneWidth;

    private Graph(final int laneWidth) {
        this.laneWidth = laneWidth;
        this.roadNodes = new LinkedHashSet<>();
        this.roads = new LinkedHashSet<>();

        this.laneNodes = new LinkedHashSet<>();
        this.lanes = new LinkedHashSet<>();

        this.unmodifiableRoadNodes = Collections.unmodifiableSet(roadNodes);
        this.unmodifiableRoads = Collections.unmodifiableSet(roads);

        this.unmodifiableLaneNodes = Collections.unmodifiableSet(laneNodes);
        this.unmodifiableLanes = Collections.unmodifiableSet(lanes);
    }

    public void generateLanes() {
        final Map<RoadNode, RoadNodeConnections> map = new HashMap<>();

        for (final RoadNode roadNode : roadNodes) {
            final RoadNodeConnections roadNodeConnections = new RoadNodeConnections();

            map.put(roadNode, roadNodeConnections);

            final double diameter = roadNode.getMostLanesConnected() * laneWidth;

            final Coordinate location = roadNode.getLocation();

            for (final Road road : roadNode.getRoads()) {
                final RoadNode connection = road.getConnectedNodeFrom(roadNode);
                final Coordinate connectionLocation = connection.getLocation();

                final Vector2D direction = new Vector2D(location, connectionLocation).normalize();

                final Vector2D translation = direction.multiply(diameter);

                final Coordinate startOfMedian = translation.translate(location);

                final Vector2D incomingLanesAngle = direction.rotateByQuarterCircle(1);
                final Vector2D outgoingLanesAngle = direction.rotateByQuarterCircle(-1);

                final LaneConnections laneConnections = new LaneConnections();

                final int totalNumberOfLanes = road.getTotalNumberOfLanes();

                final int numberOfIncomingLanes = road.getNumberOfLanesTo(roadNode);
                laneConnections.incomingLaneNodes = new LaneNode[numberOfIncomingLanes];
                final double incomingLanesOffset = ((totalNumberOfLanes / 2.0) - numberOfIncomingLanes) * laneWidth;

                for (int i = 0; i < numberOfIncomingLanes; i++) {
                    final Coordinate laneNodeLocation = incomingLanesAngle.multiply(laneWidth / 2.0 + laneWidth * i + incomingLanesOffset)
                                                                          .translate(startOfMedian);

                    final LaneNode laneNode = new LaneNode(laneNodeLocation);
                    laneConnections.incomingLaneNodes[i] = laneNode;
                }

                final int numberOfOutgoingLanes = road.getNumberOfLanesFrom(roadNode);
                laneConnections.outgoingLaneNodes = new LaneNode[numberOfOutgoingLanes];
                final double outgoingLanesOffset = ((totalNumberOfLanes / 2.0) - numberOfOutgoingLanes) * laneWidth;

                for (int i = 0; i < numberOfOutgoingLanes; i++) {
                    final Coordinate laneNodeLocation = outgoingLanesAngle.multiply(laneWidth / 2.0 + laneWidth * i + outgoingLanesOffset)
                                                                          .translate(startOfMedian);

                    final LaneNode laneNode = new LaneNode(laneNodeLocation);
                    laneConnections.outgoingLaneNodes[i] = laneNode;
                }

                roadNodeConnections.roadLaneConnections.put(road, laneConnections);
            }

            for (final Road from : roadNode.getRoads()) {
                for (final Road to : roadNode.getRoads()) {
                    if (from.equals(to)) {
                        continue;
                    }

                    final LaneNode[] fromLaneNodes = roadNodeConnections.roadLaneConnections.get(from).outgoingLaneNodes;
                    final LaneNode[] toLaneNodes = roadNodeConnections.roadLaneConnections.get(to).incomingLaneNodes;

                    for (final LaneNode fromLaneNode : fromLaneNodes) {
                        for (final LaneNode toLaneNode : toLaneNodes) {
                            final Lane lane = new Lane(fromLaneNode, toLaneNode);

                            lanes.add(lane);
                        }
                    }
                }
            }
        }

        for (final Road road : roads) {
            final RoadNode roadNode1 = road.getRoadNode1();
            final RoadNode roadNode2 = road.getRoadNode2();

            final RoadNodeConnections roadNodeConnections1 = map.get(roadNode1);
            final RoadNodeConnections roadNodeConnections2 = map.get(roadNode2);

            final LaneConnections laneConnections1 = roadNodeConnections1.roadLaneConnections.get(road);
            final LaneConnections laneConnections2 = roadNodeConnections2.roadLaneConnections.get(road);

            pairLanes(laneConnections1, laneConnections2);
            pairLanes(laneConnections2, laneConnections1);
        }

        final List<LaneNode> collect = map.values()
                                          .stream()
                                          .map(e -> e.roadLaneConnections)
                                          .map(Map::values)
                                          .flatMap(Collection::stream)
                                          .map(e -> List.of(e.incomingLaneNodes, e.outgoingLaneNodes))
                                          .flatMap(List::stream)
                                          .flatMap(Arrays::stream)
                                          .collect(Collectors.toList());

        laneNodes.addAll(collect);
    }

    private void pairLanes(final LaneConnections laneConnections1, final LaneConnections laneConnections2) {
        if (laneConnections1.incomingLaneNodes.length != laneConnections2.outgoingLaneNodes.length) {
            throw new IllegalStateException();
        }

        for (int i = 0; i < laneConnections1.incomingLaneNodes.length; i++) {
            final Lane lane = new Lane(laneConnections1.incomingLaneNodes[i], laneConnections2.outgoingLaneNodes[i]);

            lanes.add(lane);
        }
    }

    public Lane randomLane() {
        return lanes.stream()
                    .skip(ThreadLocalRandom.current().nextInt(lanes.size()))
                    .findFirst()
                    .orElseThrow();
    }

    public static Graph testGraph() {
        final RoadNode northWest = new RoadNode(new Coordinate(100, 100));
        final RoadNode southWest = new RoadNode(new Coordinate(200, 700));
        final RoadNode northEast = new RoadNode(new Coordinate(700, 300));
        final RoadNode southEast = new RoadNode(new Coordinate(730, 690));
        final RoadNode west = new RoadNode(new Coordinate(40, 500));

        final Road northWestNorthEast = Road.nLaneRoad(northWest, northEast, 1);
        final Road northEastSouthEast = Road.nLaneRoad(northEast, southEast, 1);
        final Road southEastSouthWest = Road.nLaneRoad(southEast, southWest, 1);
        final Road southWestWest = Road.nLaneRoad(southWest, west, 2);
        final Road westNorthWest = Road.nLaneRoad(west, northWest, 2);
        final Road westNorthEast = new Road(west, northEast, 3, 0);

        final Graph graph = new Graph(10);

        graph.roadNodes.add(northWest);
        graph.roadNodes.add(southWest);
        graph.roadNodes.add(northEast);
        graph.roadNodes.add(southEast);
        graph.roadNodes.add(west);

        graph.roads.add(northWestNorthEast);
        graph.roads.add(northEastSouthEast);
        graph.roads.add(southEastSouthWest);
        graph.roads.add(southWestWest);
        graph.roads.add(westNorthWest);
        graph.roads.add(westNorthEast);

        graph.generateLanes();

        return graph;
    }

    public static Graph simpleTestGraph() {
        final RoadNode northWest = new RoadNode(new Coordinate(400, 100));
        final RoadNode southWest = new RoadNode(new Coordinate(100, 700));
        final RoadNode northEast = new RoadNode(new Coordinate(700, 700));

        final Road northWestSouthWest = Road.nLaneRoad(northWest, southWest, 1);
        final Road southWestNorthEast = Road.nLaneRoad(southWest, northEast, 1);
        final Road northEastNorthWest = Road.nLaneRoad(northEast, northWest, 1);

        final Graph graph = new Graph(10);

        graph.roadNodes.add(northWest);
        graph.roadNodes.add(southWest);
        graph.roadNodes.add(northEast);

        graph.roads.add(northWestSouthWest);
        graph.roads.add(southWestNorthEast);
        graph.roads.add(northEastNorthWest);

        graph.generateLanes();

        return graph;
    }

    public Set<RoadNode> getRoadNodes() {
        return unmodifiableRoadNodes;
    }

    public Set<Road> getRoads() {
        return unmodifiableRoads;
    }

    public Set<Lane> getLanes() {
        return unmodifiableLanes;
    }

    public Set<LaneNode> getLaneNodes() {
        return unmodifiableLaneNodes;
    }

    private static class RoadNodeConnections {
        private final Map<Road, LaneConnections> roadLaneConnections;

        private RoadNodeConnections() {
            roadLaneConnections = new HashMap<>();
        }
    }

    private static class LaneConnections {
        private LaneNode[] incomingLaneNodes;
        private LaneNode[] outgoingLaneNodes;
    }
}
