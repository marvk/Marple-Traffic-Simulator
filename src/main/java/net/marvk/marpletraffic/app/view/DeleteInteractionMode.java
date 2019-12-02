package net.marvk.marpletraffic.app.view;

import net.marvk.marpletraffic.app.view.map.EditingHint;
import net.marvk.marpletraffic.app.view.map.EditingHints;
import net.marvk.marpletraffic.graph.Graph;
import net.marvk.marpletraffic.graph.Road;
import net.marvk.marpletraffic.graph.RoadNode;
import net.marvk.marpletraffic.graph.edit.GraphModification;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DeleteInteractionMode implements InteractionMode {
    private final List<EditingHint> editingHints;
    private final List<EditingHint> editingHintsUnmodifiable;

    private GraphModification delete;

    public DeleteInteractionMode() {
        this.editingHints = new ArrayList<>();
        this.editingHintsUnmodifiable = Collections.unmodifiableList(editingHints);
    }

    @Override
    public void onMouseMoved(final Coordinate mouseLocation, final Graph graph) {
        editingHints.clear();

        final Optional<RoadNode> closestRoadNode = graph.getClosestRoadNode(mouseLocation);

        if (closestRoadNode.isPresent() && closestRoadNode.get().getLocation().distance(mouseLocation) < 20) {
            EditingHints.newRoadNode(closestRoadNode.get().getLocation());
            delete = g -> g.removeRoadNode(closestRoadNode.get());
        } else {
            final Optional<Road> closestRoad = graph.getClosestRoad(mouseLocation);

            if (closestRoad.isPresent() && closestRoad.get().getLine().distance(mouseLocation) < 20) {
                EditingHints.highlightRoad(closestRoad.get());
                delete = g -> g.removeRoad(closestRoad.get());
            } else {
                delete = GraphModification.NO_OP;
            }
        }
    }

    @Override
    public void onMouseClicked(final Coordinate mouseLocation, final Graph graph) {
        if (!delete.equals(GraphModification.NO_OP)) {
            delete.apply(graph);
            graph.generateLanes();
        }
    }

    @Override
    public void abort() {

    }

    @Override
    public List<EditingHint> editingHints() {
        return editingHints;
    }
}
