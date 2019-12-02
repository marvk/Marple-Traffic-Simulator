package net.marvk.marpletraffic.app.view.control;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import net.marvk.marpletraffic.app.view.EditMode;
import net.marvk.marpletraffic.app.view.GlobalScope;

public class ControlViewModel implements ViewModel {
    @InjectScope
    private GlobalScope scope;

    public void setEditMode(final EditMode editMode) {
        scope.setEditMode(editMode);
    }
}
