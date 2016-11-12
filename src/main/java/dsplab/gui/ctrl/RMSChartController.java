package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;

import java.util.concurrent.CountDownLatch;

public interface RMSChartController extends Controller
{
    void setCaption(String s);
    void setK(int value);

    void renderRMSValues(double[] values);
    void renderAmplitudeValues(double[] values);
}
