package net.marvk.marpletraffic.app.view.map;

import javafx.scene.paint.Color;
import net.marvk.marpletraffic.graph.Road;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

public final class EditingHints {
    private static final Vector2D OFFSET = new Vector2D(0.5, 0.5);

    private EditingHints() {
        throw new AssertionError("No instances of utility class " + EditingHints.class);
    }

    public static EditingHint highlightRoad(final Road closestRoad) {
        final Coordinate l1 = OFFSET.translate(closestRoad.getRoadNode1().getLocation());
        final Coordinate l2 = OFFSET.translate(closestRoad.getRoadNode2().getLocation());

        return g -> {
            g.setLineWidth(3);
            g.setStroke(Color.INDIANRED);
            g.strokeLine(l1.getX(), l1.getY(), l2.getX(), l2.getY());
        };
    }

    public static  EditingHint newRoadNode(final Coordinate coordinate) {
        return new DrawCircle(OFFSET.translate(coordinate), Color.RED.deriveColor(1, 1, 1, 0.2), 20);
    }
}
