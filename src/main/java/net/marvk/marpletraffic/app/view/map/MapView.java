package net.marvk.marpletraffic.app.view.map;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
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

        canvas.setOnMouseMoved(event -> viewModel.mouseMoved(event.getX(), event.getY()));
        canvas.setOnMouseExited(event -> viewModel.mouseExited());
        canvas.setOnMouseClicked(event -> viewModel.mouseClicked());

        canvas.setOnKeyTyped(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                viewModel.escapePressed();
            }
        });

//        viewModel.editModeProperty().addListener((observable, oldValue, newValue) -> canvas.setCursor(getCursor()));
//        canvas.setCursor(getCursor());
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

        for (final EditingHint editingHint : viewModel.editingHints()) {
            editingHint.draw(g);
        }
    }

//    private Cursor getCursor() {
//        switch (viewModel.getEditMode()) {
//            case DELETE:
//                return Cursor.CLOSED_HAND;
//            case INSPECT:
//                return Cursor.OPEN_HAND;
//            case ADD:
//                return Cursor.CROSSHAIR;
//            default:
//                return Cursor.DEFAULT;
//        }
//    }

    private void drawAgents(final Simulation simulation, final GraphicsContext g) {
        for (final Agent agent : simulation.getAgents()) {
            g.setFill(null);
            g.setLineWidth(1);
            g.setStroke(Color.GREEN);
            final double agentX = agent.getLocation().getX() + HP;
            final double agentY = agent.getLocation().getY() + HP;
            g.strokeOval(
                    agentX - ROAD_NODE_RADIUS,
                    agentY - ROAD_NODE_RADIUS,
                    ROAD_NODE_DIAMETER,
                    ROAD_NODE_DIAMETER
            );

            final Coordinate arrow = agent.getLane().getDirectionNormal().multiply(5).translate(agent.getLocation());

            g.strokeLine(agentX, agentY, arrow.getX() + HP, arrow.getY() + HP);

            if (agent.getName() != null) {
                g.setLineWidth(1);
                g.setFont(new Font("SansSerief", 8));
                g.strokeText(agent.getName(), agent.getLocation().getX() + 3.5, agent.getLocation().getY() - 3.5);
            }
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
            g.setLineWidth(11);
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
