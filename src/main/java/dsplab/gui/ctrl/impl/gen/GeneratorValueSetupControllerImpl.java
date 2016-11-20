package dsplab.gui.ctrl.impl.gen;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Const;
import dsplab.common.Resources;
import dsplab.gui.ctrl.GeneratorValueSetupController;
import dsplab.logic.gen.modifier.ValueModifier;
import dsplab.logic.gen.modifier.ValueModifierFactory;
import dsplab.logic.gen.modifier.alg.Operation;
import dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;

import static java.lang.Double.parseDouble;

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
            ValueModifierAlgorithm.values()
        );
        this.guiAlgorithmComboBox.getSelectionModel().select(0);

        this.guiOperationComboBox.getItems().addAll(
            Operation.values()
        );
        this.guiOperationComboBox.getSelectionModel().select(0);

        // ...
    }

    public static GeneratorValueSetupController createInstance() { return
        new GeneratorValueSetupControllerImpl(); }

    // -------------------------------------------------------------------- //

    @Override
    public
    void peekValuesFrom(ValueModifier vm)
    {
        guiEnableModifierCheckBox.setSelected(vm != null);

        if (vm == null)
            return;

        guiAlgorithmComboBox.setValue(vm.getScale());
        guiOperationComboBox.setValue(vm.getOperation());

        guiStartValueField.setText(Double.toString(vm.getStartValue()));
        guiFinalValueField.setText(Double.toString(vm.getFinalValue()));

        guiEachPeriodCheckBox.setSelected(vm.isResetOnEachPeriod());
    }

    @Override
    public
    ValueModifier buildInstance() throws IllegalArgumentException
    {
        if (!guiEnableModifierCheckBox.isSelected())
            return null;

        ValueModifier vm = ValueModifierFactory.getFactory()
            .newValueModifier(guiAlgorithmComboBox.getValue());

        if (guiStartValueField.getText().isEmpty())
            guiStartValueField.setText(guiStartValueField.getPromptText());

        if (guiFinalValueField.getText().isEmpty())
            guiFinalValueField.setText(guiFinalValueField.getPromptText());

        double startValue;
        double finalValue;

        try {
            startValue = parseDouble(guiStartValueField.getText());
            finalValue = parseDouble(guiFinalValueField.getText());
        } catch (Exception cause) {
            throw new IllegalArgumentException("Invalid double", cause);
        }

        vm.setBounds(startValue, finalValue);
        vm.setOperation(guiOperationComboBox.getValue());

        vm.setResetOnEachPeriod(guiEachPeriodCheckBox.isSelected());

        return vm;
    }

    @Override
    public void setCaption(String s)
    {
        if (s != null)
            this.guiCaptionLabel.setText(s);
        else
            this.guiCaptionLabel.setText(Const.NOT_AVAILABLE_STR);
    }

    @Override
    public boolean isEnabled()
    {
        return guiEnableModifierCheckBox.isSelected();
    }

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
    private ComboBox<ValueModifierAlgorithm> guiAlgorithmComboBox;

    @FXML
    private CheckBox guiEachPeriodCheckBox;

    @FXML
    private TextField guiStartValueField;

    @FXML
    private TextField guiFinalValueField;

    @FXML
    private ComboBox<Operation> guiOperationComboBox;
}
