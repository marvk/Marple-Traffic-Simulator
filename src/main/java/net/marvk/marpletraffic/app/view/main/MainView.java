package net.marvk.marpletraffic.app.view.main;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import net.marvk.marpletraffic.app.view.map.MapViewModel;

public class MainView implements FxmlView<MainViewModel> {
    @InjectViewModel
    private MainViewModel viewModel;
}
