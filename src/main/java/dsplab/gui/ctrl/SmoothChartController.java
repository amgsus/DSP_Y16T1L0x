package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;

import java.util.function.Supplier;

public interface SmoothChartController extends Controller
{
    void setSignalSupplier(Supplier<double[]> supplier);
    void setAmplitudeSpectrumSupplier(Supplier<double[]> supplier);
    void setPhaseSpectrumSupplier(Supplier<double[]> supplier);

    void setSliSignalSupplier(Supplier<double[]> supplier);
    void setSliAmplitudeSpectrumSupplier(Supplier<double[]> supplier);
    void setSliPhaseSpectrumSupplier(Supplier<double[]> supplier);

    void setMdnSignalSupplier(Supplier<double[]> supplier);
    void setMdnAmplitudeSpectrumSupplier(Supplier<double[]> supplier);
    void setMdnPhaseSpectrumSupplier(Supplier<double[]> supplier);

    void setPblSignalSupplier(Supplier<double[]> supplier);
    void setPblAmplitudeSpectrumSupplier(Supplier<double[]> supplier);
    void setPblPhaseSpectrumSupplier(Supplier<double[]> supplier);

    void renderAll();
    void renderSignal();
    void renderSmoothedSignal();
    void renderAmplitudeSpectrum();
    void renderPhaseSpectrum();
}
