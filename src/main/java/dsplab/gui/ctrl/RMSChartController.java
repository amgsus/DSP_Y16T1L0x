package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;

import java.util.function.Supplier;

public interface RMSChartController extends Controller
{
    void setCaption(String s);
    void setK(int value);

    void setRMSValues(Supplier<double[]> supplier);
    void setAmplitudeValues(Supplier<double[]> values);

    void renderAll();
    void renderRMSValues();
    void renderAmplitudeValues();
}
