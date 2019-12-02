package net.marvk.marpletraffic.app.view.map;

import javafx.scene.canvas.GraphicsContext;

@FunctionalInterface
public interface EditHint {
    void draw(final GraphicsContext graphicsContext);
}
