package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;

public interface RMSChartController extends Controller
{
    void setCaption(String s);
    void setK(int value);

    void renderRMSValues(double[] values);
    void renderAmplitudeValues(double[] values);
}
