package dsplab.gui.ctrl.impl.gen;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Const;
import dsplab.common.Resources;
import dsplab.gui.ctrl.GeneratorValueSetupController;
import dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

import java.net.URL;

public class GeneratorValueSetupControllerImpl extends SimpleController
    implements GeneratorValueSetupController
{
    public GeneratorValueSetupControllerImpl()
    {
        try {
            URL u = Resources.GENERATOR_VALUE_SETUP_FXML;
            livenMe(u);
        } catch (Exception cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        initBindings();

        this.setCaption("Value");

        this.guiAlgorithmComboBox.getItems().addAll(
            ValueModifierAlgorithm.LINEAR,
            ValueModifierAlgorithm.LOGARITHMIC
        );
        this.guiAlgorithmComboBox.getSelectionModel().select(0);

        // ...
    }

    public static GeneratorValueSetupController createInstance() { return
        new GeneratorValueSetupControllerImpl(); }

    // -------------------------------------------------------------------- //

    @Override
    public void setCaption(String s)
    {
        if (s != null)
            this.guiCaptionLabel.setText(s);
        else
            this.guiCaptionLabel.setText(Const.NOT_AVAILABLE_STR);
    }

    // ...

    // -------------------------------------------------------------------- //

    protected
    void initBindings()
    {
        this.guiFieldsGrid.disableProperty().bind(
            this.guiEnableModifierCheckBox.selectedProperty().not());
    }

    // -------------------------------------------------------------------- //

    @FXML
    private Label guiCaptionLabel;

    @FXML
    private CheckBox guiEnableModifierCheckBox;

    @FXML
    private GridPane guiFieldsGrid;

    @FXML
    private TextField guiValueField;

    @FXML
    private ComboBox<ValueModifierAlgorithm> guiAlgorithmComboBox;

    @FXML
    private CheckBox guiEachPeriodCheckBox;

    @FXML
    private RadioButton sumRadioBtn;

    @FXML
    private ToggleGroup opRadioBtnGroup;

    @FXML
    private RadioButton multiplicationRadioBtn;
}
