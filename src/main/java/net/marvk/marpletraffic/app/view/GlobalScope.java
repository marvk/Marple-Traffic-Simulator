package net.marvk.marpletraffic.app.view;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class GlobalScope implements Scope {
    private final ObjectProperty<EditMode> editMode = new SimpleObjectProperty<>(EditMode.NONE);

    public EditMode getEditMode() {
        return editMode.get();
    }

    public ObjectProperty<EditMode> editModeProperty() {
        return editMode;
    }

    public void setEditMode(final EditMode editMode) {
        this.editMode.set(editMode);
    }
}
