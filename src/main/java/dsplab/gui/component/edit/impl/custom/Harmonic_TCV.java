package dsplab.gui.component.edit.impl.custom;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.common.Resources;
import dsplab.gui.component.edit.HarmonicTreeCellGraphic;
import dsplab.gui.util.Hei;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.enums.Waveform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

/**
 * A tree treecell graphic for item of {@link Harmonic} class.
 */
public class Harmonic_TCV extends SimpleController implements
    HarmonicTreeCellGraphic
{
    static final Image ICON_SINE = new Image(Resources.class
        .getResourceAsStream(Resources.SINE_WAVEFORM_ICON_PATH));
    static final Image ICON_COSINE = new Image(Resources.class
        .getResourceAsStream(Resources.COSINE_WAVEFORM_ICON_PATH));

    public Harmonic_TCV()
    {
        try {
            URL u = Resources.HTCV_FXML;
            livenMe(u);
        } catch (Throwable cause) {
            throw new RuntimeException("Failed to init HTCV tc", cause);
        }
    }

    @Override
    public void setData(Object item)
    {
        Harmonic data = Hei.cast(item);

        if (data != null) {

            this.guiAmplitudeLabel.textProperty().bind(data
                .getAmplitudeProperty().asString("%.3f"));
            this.guiPhaseLabel.textProperty().bind(data.getPhaseProperty()
                .asString("%.3f"));
            this.guiFrequencyLabel.textProperty().bind(data
                .getFrequencyProperty().asString("%.3f"));
            this.guiWaveformIndicatorImage.imageProperty().bind(Bindings
                .when(data.getWaveformProperty().isEqualTo(Waveform.Sine))
                .then(ICON_SINE).otherwise(ICON_COSINE));

        } else {

            this.guiAmplitudeLabel.textProperty().unbind();
            this.guiPhaseLabel.textProperty().unbind();
            this.guiFrequencyLabel.textProperty().unbind();
            this.guiWaveformIndicatorImage.imageProperty().unbind();

        }
    }

    @FXML
    protected
    ImageView guiWaveformIndicatorImage;

    @FXML
    protected
    Label guiAmplitudeLabel;

    @FXML
    protected
    Label guiPhaseLabel;

    @FXML
    protected
    Label guiFrequencyLabel;
}
