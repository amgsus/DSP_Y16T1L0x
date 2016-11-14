package dsplab.gui.ctrl.impl.edit.field;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.ctrl.HarmonicEditorController;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.enums.Waveform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
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

        initBindings();

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
            Bindings.bindBidirectional(guiAmplitudeField.textProperty(),
                data.getAmplitudeProperty(), cnv);
            Bindings.bindBidirectional(guiPhaseField.textProperty(),
                data.getPhaseProperty(), cnv);
            Bindings.bindBidirectional(guiFrequencyField.textProperty(),
                data.getFrequencyProperty(), cnv);
            guiWaveformComboBox.valueProperty().bindBidirectional(
                data.getWaveformProperty()
            );
            this.harmonic = data;
        } else {
            if (harmonic != null) {
                Bindings.unbindBidirectional(guiAmplitudeField.textProperty(),
                    harmonic.getAmplitudeProperty());
                Bindings.unbindBidirectional(guiPhaseField.textProperty(),
                    harmonic.getPhaseProperty());
                Bindings.unbindBidirectional(guiFrequencyField.textProperty(),
                    harmonic.getFrequencyProperty());
                guiWaveformComboBox.valueProperty().unbindBidirectional(
                    harmonic.getWaveformProperty()
                );
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

    protected
    void initBindings()
    {
        ObjectProperty<Waveform> w = guiWaveformComboBox.valueProperty();

        guiPhaseField.disableProperty().bind(
            w.isEqualTo(Waveform.Sawtooth).or(w.isEqualTo(Waveform.Triangle))
            .or(w.isEqualTo(Waveform.Digital))
        );
    }

    // -------------------------------------------------------------------- //

    @FXML
    private ComboBox<Waveform> guiWaveformComboBox;

    @FXML
    private TextField guiAmplitudeField;

    @FXML
    private TextField guiPhaseField;

    @FXML
    private TextField guiFrequencyField;
}
