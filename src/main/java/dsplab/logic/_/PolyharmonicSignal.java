package dsplab.logic._;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PolyharmonicSignal extends SignalLogic{

    protected List<Double> amplitudeList;
    protected List<Double> phaseList;
    protected Double frequency;

    private final Integer MAX_COUNT = 30;

    public PolyharmonicSignal(List<Double> amplitudeList, Double frequency,
                              List<Double> phaseList, Integer dimension) {
        this.amplitudeList = amplitudeList;
        this.frequency = frequency;
        this.phaseList = phaseList;
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
        Random rand = new Random();

        for (int i = 0; i < dimension; i++) {
            Double value = 0.0;
            for (int j = 0; j < MAX_COUNT; j++) {
                value += amplitudeList.get(rand.nextInt(amplitudeList.size()))
                        * Math.cos(2 * Math.PI * frequency * i
                        / dimension + phaseList.get(rand.nextInt(phaseList.size())));
            }
            signal.set(i, value);
        }
        return signal;
    }




}
