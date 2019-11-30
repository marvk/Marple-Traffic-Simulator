package net.marvk.marpletraffic.app.view.map;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import net.marvk.marpletraffic.Agent;
import net.marvk.marpletraffic.Simulation;
import net.marvk.marpletraffic.graph.Lane;
import net.marvk.marpletraffic.graph.LaneNode;
import net.marvk.marpletraffic.graph.Road;
import net.marvk.marpletraffic.graph.RoadNode;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

public class MapView implements FxmlView<MapViewModel> {
    private static final double ROAD_NODE_DIAMETER = 5;
    private static final double ROAD_NODE_RADIUS = ROAD_NODE_DIAMETER / 2;
    private static final double HP = 0.5;

    @FXML
    public Canvas canvas;

    @InjectViewModel
    private MapViewModel viewModel;

    public void initialize() {
        MvvmFX.getNotificationCenter().subscribe("REPAINT", (s, objects) -> repaint());
    }

    private void repaint() {
        final Simulation simulation = viewModel.getSimulation();

        final GraphicsContext g = canvas.getGraphicsContext2D();

        g.setFill(Color.WHEAT);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.setFill(null);
        g.setLineWidth(1);

//        drawRoads(simulation, g);
        drawLanes(simulation, g);
//        drawRoadNods(simulation, g);
//        drawLaneNodes(simulation, g);
        drawAgents(simulation, g);
    }

    private void drawAgents(final Simulation simulation, final GraphicsContext g) {
        g.setFill(null);
        g.setLineWidth(1);
        g.setStroke(Color.GREEN);
        for (final Agent agent : simulation.getAgents()) {
            g.strokeOval(
                    agent.getLocation().getX() - ROAD_NODE_RADIUS + HP,
                    agent.getLocation().getY() - ROAD_NODE_RADIUS + HP,
                    ROAD_NODE_DIAMETER,
                    ROAD_NODE_DIAMETER
            );
        }
    }

    private void drawRoads(final Simulation simulation, final GraphicsContext g) {
        g.setFill(null);
        g.setLineWidth(1);
        g.setStroke(Color.BLUE);
        for (final Road road : simulation.getGraph().getRoads()) {
            final Coordinate c1 = road.getRoadNode1().getLocation();
            final Coordinate c2 = road.getRoadNode2().getLocation();

            g.strokeLine(c1.getX() + HP, c1.getY() + HP, c2.getX() + HP, c2.getY() + HP);
        }
    }

    private void drawLaneNodes(final Simulation simulation, final GraphicsContext g) {
        g.setFill(null);
        g.setLineWidth(1);
        g.setStroke(Color.PURPLE);
        for (final LaneNode laneNode : simulation.getGraph().getLaneNodes()) {
            g.strokeOval(
                    laneNode.getLocation().getX() - ROAD_NODE_RADIUS + HP,
                    laneNode.getLocation().getY() - ROAD_NODE_RADIUS + HP,
                    ROAD_NODE_DIAMETER,
                    ROAD_NODE_DIAMETER
            );
        }
    }

    private void drawRoadNods(final Simulation simulation, final GraphicsContext g) {
        g.setFill(null);
        g.setLineWidth(1);
        g.setStroke(Color.RED);
        for (final RoadNode roadNode : simulation.getGraph().getRoadNodes()) {
            g.strokeOval(
                    roadNode.getLocation().getX() - ROAD_NODE_RADIUS + HP,
                    roadNode.getLocation().getY() - ROAD_NODE_RADIUS + HP,
                    ROAD_NODE_DIAMETER,
                    ROAD_NODE_DIAMETER
            );
        }
    }

    private void drawLanes(final Simulation simulation, final GraphicsContext g) {
        for (final Lane lane : simulation.getGraph().getLanes()) {
            final Coordinate from = lane.getFrom().getLocation();
            final Coordinate to = lane.getTo().getLocation();
            g.setLineWidth(10);
            g.setStroke(Color.DARKGRAY);
            g.setLineCap(StrokeLineCap.ROUND);
            g.strokeLine(from.getX() + HP, from.getY() + HP, to.getX() + HP, to.getY() + HP);

            final Vector2D fromTo = new Vector2D(from, to);
            final Coordinate arrow = fromTo.subtract(fromTo.normalize().multiply(10)).translate(from);
            g.setFill(Color.DARKGRAY);
            g.setStroke(null);
            g.fillOval(arrow.getX() + HP - 2.5, arrow.getY() + HP - 2.5, 5, 5);
        }
    }
}
