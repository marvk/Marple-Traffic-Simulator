package net.marvk.marpletraffic.app.view.map;

import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.util.Duration;
import net.marvk.marpletraffic.Simulation;

public class MapViewModel implements ViewModel {
    private final ReadOnlyObjectWrapper<Simulation> simulation = new ReadOnlyObjectWrapper<>();

    public MapViewModel(final Simulation simulation) {
        this.simulation.set(simulation);
    }

    public void initialize() {
        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(1./60),
                        ae -> step()
                )
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
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
}
