package dsplab.logic._;

import java.util.ArrayList;
import java.util.List;

public class HarmonicSignal extends SignalLogic {

    private Double amplitude;
    private Double frequency;
    private Double phase;

    public HarmonicSignal(Signal signal, Integer dimension) {
        this.amplitude = signal.getAmplitude();
        this.frequency = signal.getFrequency();
        this.phase = signal.getPhase();
        this.dimension = dimension;
        this.signalSpectrum = generateSignal();
        this.sineSpectrum = generateSineSpectrum(this.signalSpectrum);
        this.cosineSpectrum = generateCosineSpectrum(this.signalSpectrum);
        this.amplitudeSpectrum = generateAmplitudeSpectrum(this.sineSpectrum, this.cosineSpectrum);
        this.phaseSpectrum = generatePhaseSpectrum(this.sineSpectrum, this.cosineSpectrum);
        this.restoredSignalWithPhase = restoreWithPhaseSignal(this.amplitudeSpectrum);
        this.restoredSignalWithoutPhase = restoreSignalWithoutPhase(this.amplitudeSpectrum);
    }

    @Override
    public List<Double> generateSignal() {
        List<Double> signal = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; i++) {
            signal.set(i, amplitude * Math.cos(2 * Math.PI * frequency * i / dimension + phase));
        }
        return signal;
    }
}
