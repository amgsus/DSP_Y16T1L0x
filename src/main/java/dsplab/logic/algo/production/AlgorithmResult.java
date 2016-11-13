package dsplab.logic.algo.production;

import dsplab.logic.signal.Signal;
import javafx.geometry.Point2D;

/**
 * The algorithm produces a result for each signal.
 *
 * @see dsplab.logic.algo.AlgorithmThread
 * @see Signal
 */
public class AlgorithmResult
{
    public AlgorithmResult()
    {
    }

    public static AlgorithmResult newInstance() { return new
        AlgorithmResult(); }

    // -------------------------------------------------------------------- //

    double[] amplitudeSpectrum;
    double[] phaseSpectrum;

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

    Signal signal;
    double[] amplitudes;

    double[] rmsA;      // 0..M
    double[] rmsB;      // 0..M

    double[] ftAmplitudes;

    int sampleCount;

    // -------------------------------------------------------------------- //

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
    double[] getFtAmplitudes() { return ftAmplitudes; }

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
    double[] getIV_NoisySignal()
    {
        return noisySignal;
    }

    public
    double[] getIV_NoisyAmplitudeSpectrum()
    {
        return noisyAmplitudeSpectrum;
    }

    public
    double[] getIV_NoisyPhaseSpectrum()
    {
        return noisyPhaseSpectrum;
    }

    public
    double[] getIV_SlidingWindowSmoothedSignal()
    {
        return slidingWindowSmoothedSignal;
    }

    public
    double[] getIV_SlidingWindowSmoothedSignalAmplitudeSpectrum()
    {
        return slidingWindowSmoothedSignalAmplitudeSpectrum;
    }

    public
    double[] getIV_SlidingWindowSmoothedSignalPhaseSpectrum()
    {
        return slidingWindowSmoothedSignalPhaseSpectrum;
    }

    public
    double[] getIV_ParabolicSmoothedSignal()
    {
        return parabolicSmoothedSignal;
    }

    public
    double[] getIV_ParabolicSmoothedSignalAmplitudeSpectrum()
    {
        return parabolicSmoothedSignalAmplitudeSpectrum;
    }

    public
    double[] getIV_ParabolicSmoothedSignalPhaseSpectrum()
    {
        return parabolicSmoothedSignalPhaseSpectrum;
    }

    public
    double[] getIV_MedianSmoothedSignal()
    {
        return medianSmoothedSignal;
    }

    public
    double[] getIV_MedianSmoothedSignalAmplitudeSpectrum()
    {
        return medianSmoothedSignalAmplitudeSpectrum;
    }

    public
    double[] getIV_MedianSmoothedSignalPhaseSpectrum()
    {
        return medianSmoothedSignalPhaseSpectrum;
    }






























    public
    double[] getAmplitudes()
    {
        return amplitudes;
    }

    public
    Signal getSignal()
    {
        return signal;
    }

    public
    int getSampleCount()
    {
        return sampleCount;
    }
}
