package dsplab.gui.ctrl.impl.main;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.ctrl.MainStatusBarController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URL;

import static dsplab.common.Const.NOT_AVAILABLE_STR;
import static dsplab.common.Const.NOT_AVAILABLE_VAL;

public class MainStatusBarCtrlImpl extends SimpleController implements
    MainStatusBarController
{
    public MainStatusBarCtrlImpl()
    {
        try {
            URL u = Resources.MAIN_STATUS_BAR_FXML;
            livenMe(u);
        } catch (Throwable cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        setStatusText("");
        setNumberOfSamples(NOT_AVAILABLE_VAL);
        setNumberOfSignals(NOT_AVAILABLE_VAL);
    }

    public static MainStatusBarCtrlImpl createInstance() { return new
        MainStatusBarCtrlImpl(); }

    // -------------------------------------------------------------------- //

    @Override
    public
    void setStatusText(String text, Object... args)
    {
        String s;

        if (text == null)
            throw new IllegalArgumentException("text is null");

        if (args.length > 0)
            s = String.format(text, args);
        else
            s = text;

        guiStatusLabel.setText(s);
    }

    @Override
    public
    void setNumberOfSamples(int sampleCount)
    {
        String s;

        if (sampleCount == NOT_AVAILABLE_VAL)
            s = NOT_AVAILABLE_STR;
        else
            s = Integer.toString(sampleCount);

        guiSampleCountLabel.setText(s);
    }

    @Override
    public
    void setNumberOfSignals(int signalCount)
    {
        String s;

        if (signalCount == NOT_AVAILABLE_VAL)
            s = NOT_AVAILABLE_STR;
        else
            s = Integer.toString(signalCount);

        guiSignalCountLabel.setText(s);
    }

    @Override
    public
    void setRenderedSamplesPercentage(double percentage)
    {
        String s;

        if (Double.compare(percentage, 0) < 0) {
            s = NOT_AVAILABLE_STR;
        } else {
            if (Double.compare(percentage, 0) >= 0 &&
                Double.compare(percentage, 1) <= 0) {
                s = String.format("%.0f", percentage * 100.0);
            } else {
                s = String.format("%.0f", percentage);
            }

            s += "% rendered";
        }

        guiSamplePercentageLabel.setText("(" + s + ")");
    }

    // -------------------------------------------------------------------- //

    @FXML
    private Label guiStatusLabel;

    @FXML
    private Label guiSampleCountLabel;

    @FXML
    private Label guiSignalCountLabel;

    @FXML
    private Label guiSamplePercentageLabel;
}
