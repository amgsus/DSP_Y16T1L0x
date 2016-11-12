package dsplab.gui.component.common;

import dsplab.architecture.ctrl.util.Utils;
import dsplab.common.Resources;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

import java.net.URL;

public class WaitIndicatorBox extends VBox
{
    public WaitIndicatorBox()
    {
        try {
            URL u = Resources.WAITINDICATOR_FXML;
            Utils.fromFXML(u, this, this); // FX:ROOT
        } catch (Throwable cause) {
            throw new RuntimeException("Failed to load a FXML", cause);
        }
    }

    public void setText(String text)
    {
        guiMessageLabel.textProperty().set(text);
    }

    public StringProperty getTextProperty()
    {
        return guiMessageLabel.textProperty();
    }

    @FXML
    protected VBox guiBox;

    @FXML
    protected ProgressIndicator guiProgress;

    @FXML
    protected Label guiMessageLabel;
}
