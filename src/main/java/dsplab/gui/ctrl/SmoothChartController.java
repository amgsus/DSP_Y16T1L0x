package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;

import java.util.function.Supplier;

public interface SmoothChartController extends Controller
{
    void setSignalSupplier(Supplier<double[]> supplier);

    void setSliSignalSupplier(Supplier<double[]> supplier);
    void setSliAmplitudeSpectrumSupplier(Supplier<double[]> supplier);
    void setSliPhaseSpectrumSupplier(Supplier<double[]> supplier);

    void renderAll();
    void renderSignal();
    void renderSmoothedSignal();
    void renderAmplitudeSpectrum();
    void renderPhaseSpectrum();
}
