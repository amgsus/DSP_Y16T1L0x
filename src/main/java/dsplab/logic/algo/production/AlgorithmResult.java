package dsplab.logic.algo.production;

import dsplab.logic.signal.Signal;

/**
 * The algorithm produces a result for each signal.
 *
 * @see dsplab.logic.algo.AlgorithmThread
 * @see Signal
 */
public class AlgorithmResult
{
    public AlgorithmResult() {}

    public static AlgorithmResult newInstance() { return new
        AlgorithmResult(); }

    // -------------------------------------------------------------------- //

    int sampleCount;
    int periodCount;

    public
    int getSampleCount()
    {
        return sampleCount;
    }

    public
    int getPeriodCount()
    {
        return periodCount;
    }

    // -------------------------------------------------------------------- //

    Signal signal;

    double[] data;
    double[] amplitudeSpectrum;
    double[] phaseSpectrum;

    public
    Signal getSignal()
    {
        return signal;
    }

    public
    double[] getData()
    {
        return data;
    }

    public
    double[] getAmplitudeSpectrum()
    {
        return this.amplitudeSpectrum;
    }

    public
    double[] getPhaseSpectrum()
    {
        return this.phaseSpectrum;
    }

    // -------------------------------------------------------------------- //

    double[] rmsA;
    double[] rmsB;

    double[] rmsAmplitudes;

    public
    double[] getRMSByFormulaA()
    {
        return rmsA;
    }

    public
    double[] getRMSByFormulaB()
    {
        return rmsB;
    }

    public
    double[] getRMSAmplitudes()
    {
        return rmsAmplitudes;
    }

    // -------------------------------------------------------------------- //

    double[] restoredSignal;
    double[] restoredWithPhaseSignal;

    public
    double[] getRestoredSignal()
    {
        return this.restoredSignal;
    }

    public
    double[] getRestoredWithPhaseSignal()
    {
        return this.restoredWithPhaseSignal;
    }

    // -------------------------------------------------------------------- //

    double[] noisySignal;
    double[] noisyAmplitudeSpectrum;
    double[] noisyPhaseSpectrum;

    double[] slidingWindowSmoothedSignal;
    double[] slidingWindowSmoothedSignalAmplitudeSpectrum;
    double[] slidingWindowSmoothedSignalPhaseSpectrum;

    double[] medianSmoothedSignal;
    double[] medianSmoothedSignalAmplitudeSpectrum;
    double[] medianSmoothedSignalPhaseSpectrum;

    double[] parabolicSmoothedSignal;
    double[] parabolicSmoothedSignalAmplitudeSpectrum;
    double[] parabolicSmoothedSignalPhaseSpectrum;

    public
    double[] getNoisySignal()
    {
        return noisySignal;
    }

    public
    double[] getNoisyAmplitudeSpectrum()
    {
        return noisyAmplitudeSpectrum;
    }

    public
    double[] getNoisyPhaseSpectrum()
    {
        return noisyPhaseSpectrum;
    }

    public
    double[] getSlidingWindowSmoothedSignal()
    {
        return slidingWindowSmoothedSignal;
    }

    public
    double[] getSlidingWindowSmoothedSignalAmplitudeSpectrum()
    {
        return slidingWindowSmoothedSignalAmplitudeSpectrum;
    }

    public
    double[] getSlidingWindowSmoothedSignalPhaseSpectrum()
    {
        return slidingWindowSmoothedSignalPhaseSpectrum;
    }

    public
    double[] getParabolicSmoothedSignal()
    {
        return parabolicSmoothedSignal;
    }

    public
    double[] getParabolicSmoothedSignalAmplitudeSpectrum()
    {
        return parabolicSmoothedSignalAmplitudeSpectrum;
    }

    public
    double[] getParabolicSmoothedSignalPhaseSpectrum()
    {
        return parabolicSmoothedSignalPhaseSpectrum;
    }

    public
    double[] getMedianSmoothedSignal()
    {
        return medianSmoothedSignal;
    }

    public
    double[] getMedianSmoothedSignalAmplitudeSpectrum()
    {
        return medianSmoothedSignalAmplitudeSpectrum;
    }

    public
    double[] getMedianSmoothedSignalPhaseSpectrum()
    {
        return medianSmoothedSignalPhaseSpectrum;
    }
}
