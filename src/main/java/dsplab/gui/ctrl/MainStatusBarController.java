package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;

public interface MainStatusBarController extends Controller
{
    void setStatusText(String text, Object... args);

    void setNumberOfSamples(int sampleCount);
    void setNumberOfSignals(int signalCount);
    void setRenderedPercentage(double percentage);
    void setPeriod(int period);
}
