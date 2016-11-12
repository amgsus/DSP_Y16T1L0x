package dsplab.logic._;

import java.util.*;

public class NoisySignal extends SignalLogic {

    private Double amplitude;
    private Double frequency;
    private Double phase;
    List<Double> ps,ss,ms,asp,psp;
    private Integer msParam; //параметр сглаживания по варианту. Таблица 4 столбец 2
    private Integer ssParam; //параметр сглаживания по варианту. Таблица 4 столбец 3
    private static final Integer magicNumber = 42;

    public NoisySignal(Signal signal, Integer dimension, Integer ssParam, Integer msParam ) {
        this.amplitude = signal.getAmplitude();
        this.frequency = signal.getFrequency();
        this.phase = signal.getPhase();
        this.dimension = dimension;
        this.msParam = msParam;
        this.ssParam = ssParam;
        this.signalSpectrum = generateSignal();
        this.sineSpectrum = generateSineSpectrum(this.signalSpectrum);
        this.cosineSpectrum = generateCosineSpectrum(this.signalSpectrum);
        this.amplitudeSpectrum = generateAmplitudeSpectrum(this.sineSpectrum, this.cosineSpectrum);
        this.phaseSpectrum = generatePhaseSpectrum(this.sineSpectrum, this.cosineSpectrum);
        this.restoredSignalWithPhase = restoreWithPhaseSignal(this.amplitudeSpectrum);
        this.restoredSignalWithoutPhase = restoreSignalWithoutPhase(this.amplitudeSpectrum);
        this.ps = parabolicSmoothing();
        this.ms = medianSmoothing(this.msParam);
        this.ss = slidingSmoothing(this.ssParam);

    }
    @Override
    List<Double> generateSignal() {
        List<Double> values = new ArrayList<>(dimension);
        Random random = new Random();
        Double B = amplitude / magicNumber.doubleValue(); //magic number

        for (int i = 0; i < dimension; i++) {

            values.set(i, amplitude * Math.PI * frequency * i / dimension + phase);
            Double noise = 0.0;

            for (int j = 50; j <=70; j++) {
                noise+=Math.pow(-1,random.nextInt(2))*B*Math.sin(2*Math.PI*frequency*i*j/dimension + phase);
            }

            Double temp = values.get(i);
            values.set(i, temp + noise);
        }

        return values;
    }

    protected List<Double> parabolicSmoothing() {
        List<Double> values = new ArrayList<>(dimension);
        for (int i = 7; i < values.size()-7;i++) {
            values.set(i, (-3 * signalSpectrum.get(i - 7)
                    - 6 * signalSpectrum.get(i-6)
                    - 5 * signalSpectrum.get(i-5)
                    + 3 * signalSpectrum.get(i-4)
                    + 21 * signalSpectrum.get(i-3)
                    + 46 * signalSpectrum.get(i-2)
                    + 67 * signalSpectrum.get(i-1)
                    + 74 * signalSpectrum.get(i)
                    - 3 * signalSpectrum.get(i+7)
                    - 6 * signalSpectrum.get(i+6)
                    - 5 * signalSpectrum.get(i+5)
                    + 3 * signalSpectrum.get(i+4)
                    + 21 * signalSpectrum.get(i = 3)
                    + 46 * signalSpectrum.get(i+2)
                    + 67 * signalSpectrum.get(i+1))/320);
        }
        return values;
    }

    protected List<Double> slidingSmoothing(Integer windowSize) {
        List<Double> values = new ArrayList<>(dimension);
        List<Double> window = new ArrayList<>();
        for (int i = 0; i < values.size() - windowSize; i++) {
            window.clear();
            Double avg = 0.0;
            for (int j = i; j < i + windowSize; j++ ) {
                window.add(signalSpectrum.get(j));
                avg += signalSpectrum.get(j);
            }
            avg /= windowSize;
            values.set(i + windowSize / 2, avg);

        }
        return values;
    }

    protected List<Double> medianSmoothing(Integer windowSize) {
        List<Double> values = new ArrayList<>(dimension);
        List<Double> window = new ArrayList<>();
        for (int i = 0; i < values.size() - windowSize; i++) {
            window.clear();
            for (int j = i; j < i + windowSize; i++) {
                window.add(signalSpectrum.get(j));
            }
            Collections.sort(window);
            values.set(i + windowSize / 2, window.get(windowSize / 2 + 1));
        }
        return values;
    }

    public void operate(FilterType type) {
        List<Double> values = new ArrayList<>();
        switch (type) {
            case SLIDING:
                values = ss;
                break;
            case MEDIAN:
                values = ms;
                break;
            case PARABOLIC:
                values = ps;
                break;
        }
        List<Double> sineSpectrum = generateSineSpectrum(values);
        List<Double> cosineSpectrum = generateCosineSpectrum(values);
        asp = generateAmplitudeSpectrum(sineSpectrum, cosineSpectrum);
        psp = generatePhaseSpectrum(sineSpectrum, cosineSpectrum);


    }
}
