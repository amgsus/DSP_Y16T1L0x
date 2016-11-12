package dsplab.gui.ctrl.impl.edit.field;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.ctrl.SignalEditorController;
import dsplab.logic.signal.Signal;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;

import java.net.URL;

public class SignalEditorControllerImpl extends SimpleController implements
    SignalEditorController
{
    public
    SignalEditorControllerImpl()
    {
        try {
            URL u = Resources.SIGNALEDITOR_FIELDS_FXML;
            livenMe(u);
        } catch (Throwable cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        // ...
    }

    private Signal signal = null;

    @Override
    public
    void bind(Signal data)
    {
        if (data != null) {
            uiNameField.textProperty().bindBidirectional(
                data.getNameProperty()
            );
            uiSeriesColorBox.valueProperty().bindBidirectional(
                data.getSeriesColorProperty()
            );
            signal = data;
        } else {
            if (signal != null) {
                uiNameField.textProperty().unbindBidirectional(
                    signal.getNameProperty()
                );
                uiSeriesColorBox.valueProperty().unbindBidirectional(
                    signal.getSeriesColorProperty()
                );
                signal = null;
            }
        }
    }

    @Override
    public
    void unbind()
    {
        bind(null);
    }

    @FXML
    private
    TextField uiNameField;

    @FXML
    private
    ColorPicker uiSeriesColorBox;
}
