package net.marvk.marpletraffic.graph;

public class Road {
    private final RoadNode roadNode1;
    private final RoadNode roadNode2;

    private final int lanesFrom1To2;
    private final int lanesFrom2To1;

    public Road(final RoadNode roadNode1, final RoadNode roadNode2, final int lanesFrom1To2, final int lanesFrom2To1) {
        this.roadNode1 = roadNode1;
        this.roadNode2 = roadNode2;

        roadNode1.addRoad(this);
        roadNode2.addRoad(this);

        this.lanesFrom1To2 = lanesFrom1To2;
        this.lanesFrom2To1 = lanesFrom2To1;
    }

    public RoadNode getRoadNode1() {
        return roadNode1;
    }

    public RoadNode getRoadNode2() {
        return roadNode2;
    }

    public int getLanesFrom1To2() {
        return lanesFrom1To2;
    }

    public int getLanesFrom2To1() {
        return lanesFrom2To1;
    }

    public int getTotalNumberOfLanes() {
        return lanesFrom1To2 + lanesFrom2To1;
    }

    public int getNumberOfLanesFrom(final RoadNode roadNode) {
        if (roadNode1 == roadNode) {
            return lanesFrom1To2;
        }

        if (roadNode2 == roadNode) {
            return lanesFrom2To1;
        }

        throw new IllegalArgumentException("RoadNode " + roadNode + " is not connected to road");
    }

    public int getNumberOfLanesTo(final RoadNode roadNode) {
        if (roadNode1 == roadNode) {
            return lanesFrom2To1;
        }

        if (roadNode2 == roadNode) {
            return lanesFrom1To2;
        }

        throw new IllegalArgumentException("RoadNode " + roadNode + " is not connected to road");
    }

    public static Road nLaneRoad(final RoadNode roadNode1, final RoadNode roadNode2, final int n) {
        return new Road(roadNode1, roadNode2, n, n);
    }

    @Override
    public String toString() {
        return "Road{" +
                "roadNode1=" + roadNode1.getLocation() +
                ", roadNode2=" + roadNode2.getLocation() +
                ", lanesFrom1To2=" + lanesFrom1To2 +
                ", lanesFrom2To1=" + lanesFrom2To1 +
                '}';
    }

    public RoadNode getConnectedNodeFrom(final RoadNode roadNode) {
        if (roadNode == roadNode1) {
            return roadNode2;
        }
        if (roadNode == roadNode2) {
            return roadNode1;
        }

        throw new IllegalArgumentException("RoadNode " + roadNode + " is not connected to road");
    }
}
