package net.marvk.marpletraffic.app.view;

import net.marvk.marpletraffic.app.view.map.EditingHint;
import net.marvk.marpletraffic.graph.Graph;
import org.locationtech.jts.geom.Coordinate;

import java.util.Collections;
import java.util.List;

public class InspectInteractionMode implements InteractionMode {
    @Override
    public void onMouseMoved(final Coordinate mouseLocation, final Graph graph) {

    }

    @Override
    public void onMouseClicked(final Coordinate mouseLocation, final Graph graph) {

    }

    @Override
    public void abort() {

    }

    @Override
    public List<EditingHint> editingHints() {
        return Collections.emptyList();
    }
}
