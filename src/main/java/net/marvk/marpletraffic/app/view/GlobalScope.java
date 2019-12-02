package net.marvk.marpletraffic.app.view;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class GlobalScope implements Scope {
    private final ObjectProperty<InteractionModeSupplier> interactionModeSupplier = new SimpleObjectProperty<>(() -> InteractionMode.NEUTRAL);

    public InteractionModeSupplier getInteractionModeSupplier() {
        return interactionModeSupplier.get();
    }

    public ObjectProperty<InteractionModeSupplier> interactionModeSupplierProperty() {
        return interactionModeSupplier;
    }

    public void setInteractionModeSupplier(final InteractionModeSupplier interactionModeSupplier) {
        this.interactionModeSupplier.set(interactionModeSupplier);
    }
}
