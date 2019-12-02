package net.marvk.marpletraffic.app.view;

import javafx.scene.paint.Color;
import net.marvk.marpletraffic.app.view.map.DrawCircle;
import net.marvk.marpletraffic.app.view.map.EditingHint;
import net.marvk.marpletraffic.app.view.map.EditingHints;
import net.marvk.marpletraffic.graph.Graph;
import net.marvk.marpletraffic.graph.Road;
import net.marvk.marpletraffic.graph.RoadNode;
import net.marvk.marpletraffic.graph.edit.NewRoad;
import net.marvk.marpletraffic.graph.edit.RoadNodeStartPoint;
import net.marvk.marpletraffic.graph.edit.RoadSplit;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AddInteractionMode implements InteractionMode {
    private static final Vector2D OFFSET = new Vector2D(0.5, 0.5);

    private final List<EditingHint> editingHints;
    private final List<EditingHint> editingHintsUnmodifiable;

    private NewRoad newRoad = new NewRoad();

    private RoadSplit potentialSplit;
    private RoadNode potentialRoadNode;

    public AddInteractionMode() {
        this.editingHints = new ArrayList<>();
        this.editingHintsUnmodifiable = Collections.unmodifiableList(editingHints);
    }

    @Override
    public void onMouseMoved(final Coordinate mouseLocation, final Graph graph) {
        editingHints.clear();

        final Optional<RoadNode> closestRoadNode = graph.getClosestRoadNode(mouseLocation);

        if (closestRoadNode.isPresent() && closestRoadNode.get().distance(mouseLocation) < 20) {
            editingHints.add(EditingHints.newRoadNode(closestRoadNode.get().getLocation()));
            potentialRoadNode = closestRoadNode.get();
        } else {
            potentialRoadNode = null;
        }

        if (potentialRoadNode == null) {
            final Optional<Road> closestRoad = graph.getClosestRoad(mouseLocation);

            final Optional<Coordinate> closestPointOnClosestRoad =
                    closestRoad.map(Road::getLine)
                               .map(e -> e.closestPoint(mouseLocation));

            final boolean closestRoadIsCloseEnough =
                    closestRoad.map(Road::getLine)
                               .filter(e -> e.distance(mouseLocation) < 20)
                               .isPresent();

            if (closestRoad.isPresent() && closestRoadIsCloseEnough) {
                editingHints.add(EditingHints.highlightRoad(closestRoad.get()));
                editingHints.add(EditingHints.newRoadNode(closestPointOnClosestRoad.get()));

                potentialSplit = new RoadSplit(closestRoad.get(), closestPointOnClosestRoad.get());
            } else {
                potentialSplit = null;
            }
        }

        if (newRoad.numberOfPoints() == 1) {
            final Coordinate firstSplitLocation = OFFSET.translate(newRoad.getRoadStartPoint(0).getLocation());

            editingHints.add(new DrawCircle(firstSplitLocation, Color.RED, 5));

            final Coordinate potentialSecondRoadNodeLocation;

            if (potentialRoadNode != null) {
                potentialSecondRoadNodeLocation = potentialRoadNode.getLocation();
            } else if (potentialSplit != null) {
                potentialSecondRoadNodeLocation = potentialSplit.getLocation();
            } else {
                potentialSecondRoadNodeLocation = mouseLocation;
            }

            final Coordinate potentialSecondRoadNodeLocationOffset = OFFSET.translate(potentialSecondRoadNodeLocation);

            editingHints.add(g -> {
                g.setLineWidth(2);
                g.setStroke(Color.RED);
                g.strokeLine(
                        firstSplitLocation.getX(),
                        firstSplitLocation.getY(),
                        potentialSecondRoadNodeLocationOffset.getX(),
                        potentialSecondRoadNodeLocationOffset.getY()
                );
            });
        }
    }

    @Override
    public void onMouseClicked(final Coordinate mouseLocation, final Graph graph) {
        if (potentialRoadNode != null) {
            newRoad.add(new RoadNodeStartPoint(potentialRoadNode));
        } else if (potentialSplit != null) {
            newRoad.add(potentialSplit);
        } else {
            final RoadNode roadNode = new RoadNode(mouseLocation);
            newRoad.add(() -> roadNode);
        }

        potentialSplit = null;
        potentialRoadNode = null;

        if (newRoad.isApplicable()) {
            newRoad.apply(graph);
            graph.generateLanes();
            newRoad = new NewRoad();
        }
    }

    @Override
    public void abort() {
        newRoad = new NewRoad();
        potentialRoadNode = null;
        potentialSplit = null;
    }

    @Override
    public List<EditingHint> editingHints() {
        return editingHintsUnmodifiable;
    }
}
