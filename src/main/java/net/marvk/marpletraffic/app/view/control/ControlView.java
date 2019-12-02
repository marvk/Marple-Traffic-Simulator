package net.marvk.marpletraffic.app.view.control;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import net.marvk.marpletraffic.app.view.AddInteractionMode;
import net.marvk.marpletraffic.app.view.DeleteInteractionMode;
import net.marvk.marpletraffic.app.view.InspectInteractionMode;
import net.marvk.marpletraffic.app.view.InteractionModeSupplier;

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
        initializeToggleButton(inspectButton, InspectInteractionMode::new);
        initializeToggleButton(addButton, AddInteractionMode::new);
        initializeToggleButton(deleteButton, DeleteInteractionMode::new);

        inspectButton.setSelected(true);

        toggleGroup.selectedToggleProperty()
                   .addListener((observable, oldValue, newValue) -> setInteractionModeSupplier(newValue));

        setInteractionModeSupplier(toggleGroup.getSelectedToggle());
    }

    private void setInteractionModeSupplier(final Toggle selectedToggle) {
        viewModel.setInteractionModeSupplier((InteractionModeSupplier) selectedToggle.getUserData());
    }

    private void initializeToggleButton(final ToggleButton toggleButton, final InteractionModeSupplier interactionModeSupplier) {
        toggleButton.setToggleGroup(toggleGroup);
        toggleButton.setUserData(interactionModeSupplier);
    }

    @FXML
    public void spawn(final ActionEvent actionEvent) {
        viewModel.spawn();
    }
}
