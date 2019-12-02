package net.marvk.marpletraffic.app.view.map;

import javafx.scene.canvas.GraphicsContext;

@FunctionalInterface
public interface EditingHint {
    void draw(final GraphicsContext graphicsContext);
}
