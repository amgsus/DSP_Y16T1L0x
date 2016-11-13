package dsplab.gui.ctrl.impl.edit.field;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.ctrl.HarmonicEditorController;
import dsplab.gui.util.Hei;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.enums.Waveform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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

        guiWaveformComboBox.getItems().addAll(Waveform.values());
    }

    private Harmonic harmonic;

    @SuppressWarnings("unchecked")
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
            guiWaveformComboBox.valueProperty().bindBidirectional(
                data.getWaveformProperty()
            );
            this.harmonic = data;
        } else {
            if (harmonic != null) {
                Bindings.unbindBidirectional(uiAmplitudeField.textProperty(),
                    harmonic.getAmplitudeProperty());
                Bindings.unbindBidirectional(uiPhaseField.textProperty(),
                    harmonic.getPhaseProperty());
                Bindings.unbindBidirectional(uiFrequencyField.textProperty(),
                    harmonic.getFrequencyProperty());
                guiWaveformComboBox.valueProperty().unbind();
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
    private ComboBox<Waveform> guiWaveformComboBox;

    @FXML
    private TextField uiAmplitudeField;

    @FXML
    private TextField uiPhaseField;

    @FXML
    private TextField uiFrequencyField;
}
