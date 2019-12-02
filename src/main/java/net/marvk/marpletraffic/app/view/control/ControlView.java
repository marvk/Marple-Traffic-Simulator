package net.marvk.marpletraffic.app.view.control;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import net.marvk.marpletraffic.app.view.EditMode;

public class ControlView implements FxmlView<ControlViewModel> {
    @FXML
    public ToggleButton inspectButton;

    @FXML
    public ToggleButton addButton;

    @FXML
    public ToggleButton deleteButton;

    @InjectViewModel
    private ControlViewModel viewModel;

    private final ToggleGroup toggleGroup;

    public ControlView() {
        this.toggleGroup = new ToggleGroup();
    }

    public void initialize() {
        initializeToggleButton(inspectButton, EditMode.INSPECT);
        initializeToggleButton(addButton, EditMode.ADD);
        initializeToggleButton(deleteButton, EditMode.DELETE);

        inspectButton.setSelected(true);

        toggleGroup.selectedToggleProperty()
                   .addListener((observable, oldValue, newValue) -> setEditMode(newValue));

        setEditMode(toggleGroup.getSelectedToggle());
    }

    private void setEditMode(final Toggle selectedToggle) {
        viewModel.setEditMode((EditMode) selectedToggle.getUserData());
    }

    private void initializeToggleButton(final ToggleButton toggleButton, final EditMode mode) {
        toggleButton.setToggleGroup(toggleGroup);
        toggleButton.setUserData(mode);
    }
}
