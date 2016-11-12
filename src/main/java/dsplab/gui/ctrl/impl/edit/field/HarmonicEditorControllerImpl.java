package dsplab.gui.ctrl.impl.edit.field;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.ctrl.HarmonicEditorController;
import dsplab.logic.signal.Harmonic;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;

public class HarmonicEditorControllerImpl extends SimpleController implements
    HarmonicEditorController
{
    public HarmonicEditorControllerImpl()
    {
        try {
            URL u = Resources.HARMONICEDITOR_FIELDS_FXML;
            livenMe(u);
        } catch (Throwable cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }
    }

    private Harmonic harmonic;

    @Override
    public
    void bind(Harmonic data)
    {
        if (data != null) {
            StringConverter<Number> cnv = new NumberStringConverter();
            Bindings.bindBidirectional(uiAmplitudeField.textProperty(),
                data.getAmplitudeProperty(), cnv);
            Bindings.bindBidirectional(uiPhaseField.textProperty(),
                data.getPhaseProperty(), cnv);
            Bindings.bindBidirectional(uiFrequencyField.textProperty(),
                data.getFrequencyProperty(), cnv);
            // ...
            this.harmonic = data;
        } else {
            if (harmonic != null) {
                // ...
                Bindings.unbindBidirectional(uiAmplitudeField.textProperty(),
                    harmonic.getAmplitudeProperty());
                Bindings.unbindBidirectional(uiPhaseField.textProperty(),
                    harmonic.getPhaseProperty());
                Bindings.unbindBidirectional(uiFrequencyField.textProperty(),
                    harmonic.getFrequencyProperty());
                harmonic = null;
            }
        }
    }

    @Override
    public
    void unbind()
    {
        bind(null);
    }

    // -------------------------------------------------------------------- //

    @FXML
    private RadioButton uiSineWaveformRadioBtn;

    @FXML
    private ToggleGroup waveformGroup;

    @FXML
    private RadioButton uiCosineWaveformRadioBtn;

    @FXML
    private TextField uiAmplitudeField;

    @FXML
    private TextField uiPhaseField;

    @FXML
    private TextField uiFrequencyField;

    @FXML
    private RadioButton guiSineNoiseWaveformRadioBtn;
}
