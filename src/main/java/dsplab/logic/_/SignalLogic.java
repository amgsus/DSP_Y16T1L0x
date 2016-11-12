package dsplab.logic._;

import java.util.ArrayList;
import java.util.List;

public abstract class SignalLogic {

    protected List<Double> sineSpectrum;
    protected List<Double> cosineSpectrum;
    protected List<Double> amplitudeSpectrum;
    protected List<Double> phaseSpectrum;
    protected List<Double> signalSpectrum;
    protected List<Double> restoredSignalWithPhase;
    protected List<Double> restoredSignalWithoutPhase;
    protected Integer dimension;

    public List<Double> getAmplitudeSpectrum() {
        return amplitudeSpectrum;
    }

    public List<Double> getPhaseSpectrum() {
        return phaseSpectrum;
    }

    public List<Double> getSignalSpectrum() {
        return signalSpectrum;
    }

    public List<Double> getRestoredSignalWithPhase() {
        return restoredSignalWithPhase;
    }

    public List<Double> getRestoredSignalWithoutPhase() {
        return restoredSignalWithoutPhase;
    }

    SignalLogic() {
        signalSpectrum = generateSignal();
        sineSpectrum = generateSineSpectrum(this.signalSpectrum);
        cosineSpectrum = generateCosineSpectrum(this.signalSpectrum);
        amplitudeSpectrum = generateAmplitudeSpectrum(this.sineSpectrum, this.cosineSpectrum);
        phaseSpectrum = generatePhaseSpectrum(this.sineSpectrum, this.cosineSpectrum);
        restoredSignalWithPhase = restoreWithPhaseSignal(this.amplitudeSpectrum);
        restoredSignalWithoutPhase = restoreSignalWithoutPhase(this.amplitudeSpectrum);
    }

    List<Double> generateSignal() {
        return null;
    }

    List<Double> generateSineSpectrum(List<Double> signalSpectrum) {
        List<Double> values = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; i++) {
            Double value = 0.0;
            for (int j = 0; j < dimension; j++) {
                value += signalSpectrum.get(j) * Math.sin(2 * Math.PI * i * j / dimension);
            }
            values.set(i, (2 * value) / dimension);
        }
        return values;
    }

    List<Double> generateCosineSpectrum(List<Double> signalSpectrum) {
        List<Double> values = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; i++) {
            Double value = 0.0;
            for (int j = 0; j < dimension; j++) {
                value += signalSpectrum.get(j) * Math.cos(2 * Math.PI * i * j / dimension);
            }
            values.set(i, (2 * value) / dimension);
        }
        return values;
    }

    List<Double> generateAmplitudeSpectrum(List<Double> sineSpectrum, List<Double> cosineSpectrum) {
        List<Double> values = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; i++) {
            values.set(i, Math.sqrt(Math.pow(sineSpectrum.get(i), 2) + Math.pow(cosineSpectrum.get(i), 2)));
        }
        return values;
    }

    List<Double> generatePhaseSpectrum(List<Double> sineSpectrum, List<Double> cosineSpectrum) {
        List<Double> values = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; i++) {
            values.set(i, Math.atan(sineSpectrum.get(i) / cosineSpectrum.get(i)));
        }
        return values;
    }

    List<Double> restoreWithPhaseSignal(List<Double> amplitudeSpectrum) {
        List<Double> values = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; i++) {
            Double value = 0.0;
            for (int j = 0; j < dimension; j++) {
                value += amplitudeSpectrum.get(i) * Math.cos(2 * Math.PI * i * j / dimension - phaseSpectrum.get(j));
            }
            values.set(i, value);
        }
        return values;
    }

    List<Double> restoreSignalWithoutPhase(List<Double> amplitudeSpectrum) {
        List<Double> values = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; i++) {
            Double value = 0.0;
            for (int j = 0; j < dimension; j++) {
                value += amplitudeSpectrum.get(i) * Math.cos(2 * Math.PI * i * j / dimension);
            }
            values.set(i, value);
        }
        return values;
    }
}
