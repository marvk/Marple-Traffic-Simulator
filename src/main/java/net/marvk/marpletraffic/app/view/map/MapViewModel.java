package net.marvk.marpletraffic.app.view.map;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import net.marvk.marpletraffic.Simulation;
import net.marvk.marpletraffic.app.view.EditMode;
import net.marvk.marpletraffic.app.view.GlobalScope;
import net.marvk.marpletraffic.graph.Road;
import net.marvk.marpletraffic.graph.edit.RoadSplit;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

import java.util.Comparator;
import java.util.Optional;

public class MapViewModel implements ViewModel {
    private static final Vector2D OFFSET = new Vector2D(0.5, 0.5);
    private final ReadOnlyObjectWrapper<Simulation> simulation = new ReadOnlyObjectWrapper<>();

    private final ReadOnlyObjectWrapper<EditMode> editMode = new ReadOnlyObjectWrapper<>();

    private final ObservableList<EditHint> editHints = FXCollections.observableArrayList();

    private final ObservableList<EditHint> editHintsUnmodifiable = FXCollections.unmodifiableObservableList(editHints);

    private Coordinate mousePosition;

    private RoadSplit potentialSplit;
    private RoadSplit firstSplit;

    @InjectScope
    private GlobalScope scope;

    public MapViewModel(final Simulation simulation) {
        this.simulation.set(simulation);
    }

    public void initialize() {
        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(1. / 60),
                        ae -> step()
                )
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        editMode.bind(scope.editModeProperty());
    }

    private void step() {
        simulation.get().step();
        MvvmFX.getNotificationCenter().publish("REPAINT");
    }

    public Simulation getSimulation() {
        return simulation.get();
    }

    public ReadOnlyObjectProperty<Simulation> simulationProperty() {
        return simulation.getReadOnlyProperty();
    }

    public void mouseMoved(final double x, final double y) {
        editHints.clear();

        mousePosition = new Coordinate(x, y);

        final Optional<Road> closestRoad =
                simulation.get()
                          .getGraph()
                          .getRoads()
                          .stream()
                          .min(Comparator.comparingDouble(r -> r.getLine().distance(mousePosition)));

        final Optional<Coordinate> closestPointOnClosestRoad =
                closestRoad.map(Road::getLine)
                           .map(e -> e.closestPoint(mousePosition));

        final boolean closestRoadIsCloseEnough =
                closestRoad.map(Road::getLine)
                           .map(e -> e.distance(mousePosition) < 20)
                           .orElse(false);

        if (closestRoad.isPresent() && closestRoadIsCloseEnough) {
            final Coordinate l1 = OFFSET.translate(closestRoad.get().getRoadNode1().getLocation());
            final Coordinate l2 = OFFSET.translate(closestRoad.get().getRoadNode2().getLocation());

            editHints.add(g -> {
                g.setLineWidth(3);
                g.setStroke(Color.INDIANRED);
                g.strokeLine(l1.getX(), l1.getY(), l2.getX(), l2.getY());
            });

            final Coordinate closestPointOffset = OFFSET.translate(closestPointOnClosestRoad.get());

            editHints.add(
                    new DrawCircle(closestPointOffset, Color.RED.deriveColor(1, 1, 1, 0.2), 20)
            );
            potentialSplit = new RoadSplit(closestRoad.get(), closestPointOnClosestRoad.get());
        } else {
            potentialSplit = null;
        }

        if (firstSplit != null) {
            final Coordinate firstSplitLocation = OFFSET.translate(firstSplit.getLocation());

            editHints.add(new DrawCircle(firstSplitLocation, Color.RED, 5));

            final Coordinate potentialSecondRoadNodeLocation =
                    closestRoadIsCloseEnough ? closestPointOnClosestRoad.get() : mousePosition;

            final Coordinate potentialSecondRoadNodeLocationOffset = OFFSET.translate(potentialSecondRoadNodeLocation);

            editHints.add(g -> {
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

    public void mouseExited() {
        mousePosition = null;
    }

    public void mouseClicked() {
        if (firstSplit == null) {
            firstSplit = potentialSplit;
            potentialSplit = null;
        } else {
            getSimulation().getGraph().addNewRoad(firstSplit, potentialSplit);
            getSimulation().getGraph().generateLanes();

            firstSplit = null;
            potentialSplit = null;
        }
    }

    public void escapePressed() {

    }

    public EditMode getEditMode() {
        return scope.getEditMode();
    }

    public ReadOnlyObjectProperty<EditMode> editModeProperty() {
        return editMode.getReadOnlyProperty();
    }

    public ObservableList<EditHint> editHints() {
        return editHintsUnmodifiable;
    }

    private static class DrawCircle implements EditHint {
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
}
