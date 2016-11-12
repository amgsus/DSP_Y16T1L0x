package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;

import java.util.function.Supplier;

public interface SpectrumController extends Controller
{
    void setAmplitudeSpectrumData(Supplier<double[]> s);
    void setPhaseSpectrumData(Supplier<double[]> s);

    void renderAll();
    void renderAmplitudeSpectrum();
    void renderPhaseSpectrum();
}
