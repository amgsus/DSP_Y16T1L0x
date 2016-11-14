package dsplab.gui.ctrl.impl;

import dsplab.architecture.callback.Delegate;
import dsplab.architecture.callback.DelegateWrapper;
import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.prop.TimelineProperties;
import dsplab.gui.ctrl.TimelineSetupController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;

public class TimelineSetupControllerImpl extends SimpleController implements
    TimelineSetupController
{
    public TimelineSetupControllerImpl()
    {
        try {
            URL u = Resources.TIMELINE_SETUP_SCENE_FXML;
            livenMe(u);
        } catch (Throwable cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        initComponents();
    }

    public static TimelineSetupControllerImpl createInstance() { return new
        TimelineSetupControllerImpl(); }

    // -------------------------------------------------------------------- //

    private
    TimelineProperties editingProperties;

    @Override
    public void setEditableProps(TimelineProperties properties)
    {
        if (properties == null)
            throw new IllegalArgumentException("Properties not specified");

        guiPeriodComboBox.setValue(properties.getPeriodsProperty().get());
        guiSamplesCombo.setValue(properties.getSamplesProperty().get());

        this.editingProperties = properties;
    }

    // -------------------------------------------------------------------- //

    protected final
    DelegateWrapper okDelegateWrap = new DelegateWrapper();

    protected final
    DelegateWrapper cancelDelegateWrap = new DelegateWrapper();

    @Override
    public void setOnOK(Delegate delegate)
    {
        if (delegate != null) {
            okDelegateWrap.wrapDelegate(delegate);
        } else {
            okDelegateWrap.removeDelegate();
        }
    }

    @Override
    public void setOnCancel(Delegate delegate)
    {
        if (delegate != null) {
            cancelDelegateWrap.wrapDelegate(delegate);
        } else {
            cancelDelegateWrap.removeDelegate();
        }
    }

    // -------------------------------------------------------------------- //

    protected
    void initComponentsHandlers()
    {
        guiOKButton.setOnAction(event -> {

            if (editingProperties != null) {
                this.editingProperties.getPeriodsProperty().set(
                    guiPeriodComboBox.valueProperty().get()
                );
                this.editingProperties.getSamplesProperty().set(
                    guiSamplesCombo.valueProperty().get()
                );
                this.editingProperties = null;
            } else {
                // ToDo: Log: No properties set.
            }

            okDelegateWrap.execute();
        });

        guiCancelButton.setOnAction(event -> cancelDelegateWrap.execute());
    }

    protected
    void initBindings()
    {
        guiSamplesCombo.disableProperty().bind(
            guiManualSampleCountRadioBtn.selectedProperty().not()
        );
    }

    protected
    void initComponents()
    {
        guiSamplesCombo.getItems().addAll(32, 64, 128, 256, 512, 1024, 2048,
            4096, 8192);
        guiSamplesCombo.getSelectionModel().select(4);

        guiPeriodComboBox.getItems().addAll(1, 2, 3, 4, 5);
        guiPeriodComboBox.getSelectionModel().select(0);

        initBindings();
        initComponentsHandlers();
    }

    // -------------------------------------------------------------------- //

    @FXML
    private RadioButton guiAutoSampleCountRadioBtn;

    @FXML
    private ToggleGroup sampleModeBtnGroup;

    @FXML
    private RadioButton guiManualSampleCountRadioBtn;

    @FXML
    private ComboBox<Integer> guiSamplesCombo;

    @FXML
    private Button guiOKButton;

    @FXML
    private Button guiCancelButton;

    @FXML
    private ComboBox<Integer> guiPeriodComboBox;
}
