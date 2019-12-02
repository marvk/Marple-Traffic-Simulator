package net.marvk.marpletraffic.app.view.map;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import net.marvk.marpletraffic.Simulation;
import net.marvk.marpletraffic.app.view.GlobalScope;
import net.marvk.marpletraffic.app.view.InteractionMode;
import net.marvk.marpletraffic.graph.Graph;
import org.locationtech.jts.geom.Coordinate;

public class MapViewModel implements ViewModel {
    private final ReadOnlyObjectWrapper<Simulation> simulation = new ReadOnlyObjectWrapper<>();

    private final ReadOnlyObjectWrapper<InteractionMode> interactionMode = new ReadOnlyObjectWrapper<>();

    private final ObservableList<EditingHint> editingHints = FXCollections.observableArrayList();

    private final ObservableList<EditingHint> editingHintsUnmodifiable = FXCollections.unmodifiableObservableList(editingHints);

    private Coordinate mouseLocation;

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

        interactionMode.bind(Bindings.createObjectBinding(
                () -> scope.getInteractionModeSupplier().get(),
                scope.interactionModeSupplierProperty()
        ));
    }

    private void step() {
        simulation.get().step();
        MvvmFX.getNotificationCenter().publish("REPAINT");
    }

    public void mouseMoved(final double x, final double y) {
        mouseLocation = new Coordinate(x, y);
        editingHints.clear();

        interactionMode.get().onMouseMoved(mouseLocation, graph());
        editingHints.setAll(interactionMode.get().editingHints());
    }

    public void mouseClicked() {
        interactionMode.get().onMouseClicked(mouseLocation, graph());
    }

    public void escapePressed() {
        interactionMode.get().abort();
    }

    private Graph graph() {
        return getSimulation().getGraph();
    }

    public void mouseExited() {
        mouseLocation = null;
    }

    public Simulation getSimulation() {
        return simulation.get();
    }

    public ReadOnlyObjectProperty<Simulation> simulationProperty() {
        return simulation.getReadOnlyProperty();
    }

    public InteractionMode getInteractionMode() {
        return interactionMode.get();
    }

    public ReadOnlyObjectProperty<InteractionMode> interactionModeProperty() {
        return interactionMode.getReadOnlyProperty();
    }

    public ObservableList<EditingHint> editingHints() {
        return editingHintsUnmodifiable;
    }
}
