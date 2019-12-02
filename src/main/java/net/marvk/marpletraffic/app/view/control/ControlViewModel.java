package net.marvk.marpletraffic.app.view.control;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import net.marvk.marpletraffic.Simulation;
import net.marvk.marpletraffic.app.view.GlobalScope;
import net.marvk.marpletraffic.app.view.InteractionModeSupplier;

public class ControlViewModel implements ViewModel {
    private final Simulation simulation;

    @InjectScope
    private GlobalScope scope;

    public ControlViewModel(final Simulation simulation) {
        this.simulation = simulation;
    }

    public void setInteractionModeSupplier(final InteractionModeSupplier interactionModeSupplier) {
        scope.setInteractionModeSupplier(interactionModeSupplier);
    }

    public void spawn() {
        simulation.spawnAgents();
    }
}
