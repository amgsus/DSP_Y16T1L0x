package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;

import java.util.function.Supplier;

public interface EQController extends Controller
{
    void setSignal(Supplier<double[]> supplier);
    void setLPFilteredSignal(Supplier<double[]> supplier);
    void setHPFilteredSignal(Supplier<double[]> supplier);
    void setBPFilteredSignal(Supplier<double[]> supplier);

    void setSrcAmplitudeSpectrum(Supplier<double[]> supplier);
    void setSrcPhaseSpectrum(Supplier<double[]> supplier);
    void setLPFAmplitudeSpectrum(Supplier<double[]> supplier);
    void setLPFPhaseSpectrum(Supplier<double[]> supplier);
    void setHPFAmplitudeSpectrum(Supplier<double[]> supplier);
    void setHPFPhaseSpectrum(Supplier<double[]> supplier);
    void setBPFAmplitudeSpectrum(Supplier<double[]> supplier);
    void setBPFPhaseSpectrum(Supplier<double[]> supplier);

    void renderAll();
    void renderSourceSignal();
    void renderEQSignal();
    void renderAmplitudeSpectrum();
    void renderPhaseSpectrum();
}
