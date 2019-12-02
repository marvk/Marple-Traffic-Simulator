package net.marvk.marpletraffic.app.view.map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.locationtech.jts.geom.Coordinate;

public class DrawCircle implements EditingHint {
    private final Coordinate firstSplitLocation;
    private final Color color;
    private final double radius;

    public DrawCircle(final Coordinate firstSplitLocation, final Color color, final double radius) {
        this.firstSplitLocation = firstSplitLocation;
        this.color = color;
        this.radius = radius;
    }

    @Override
    public void draw(final GraphicsContext g) {
        g.setFill(color);
        g.fillOval(firstSplitLocation.getX() - radius, firstSplitLocation.getY() - radius, radius * 2, radius * 2);
    }
}