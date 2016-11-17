package dsplab.logic.algo.production;

import dsplab.logic.signal.Signal;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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

    final AtomicReference<Signal> signal = new AtomicReference<>();

    public
    Signal getSignal()
    {
        return signal.get();
    }

    final AtomicInteger sampleCount = new AtomicInteger();
    final AtomicInteger periodCount = new AtomicInteger();

    public
    int getSampleCount()
    {
        return sampleCount.get();
    }

    public
    int getPeriodCount()
    {
        return periodCount.get();
    }

    final AtomicReference<double[]> data = new AtomicReference<>();
    final AtomicReference<double[]> amplitudeSpectrum
        = new AtomicReference<>();
    final AtomicReference<double[]> phaseSpectrum
        = new AtomicReference<>();

    public
    double[] getData()
    {
        return data.get();
    }

    public
    double[] getAmplitudeSpectrum()
    {
        return amplitudeSpectrum.get();
    }

    public
    double[] getPhaseSpectrum()
    {
        return phaseSpectrum.get();
    }

    // -------------------------------------------------------------------- //

    volatile double[] rmsA;
    volatile double[] rmsB;

    volatile double[] rmsAmplitudes;

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

    volatile double[] restoredSignal;
    volatile double[] restoredWithPhaseSignal;

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

    final AtomicReference<double[]> noisySignal = new AtomicReference<>();
    final AtomicReference<double[]> noisyAmplitudeSpectrum
        = new AtomicReference<>();
    final AtomicReference<double[]> noisyPhaseSpectrum
        = new AtomicReference<>();

    volatile double[] slidingWindowSmoothedSignal;
    volatile double[] slidingWindowSmoothedSignalAmplitudeSpectrum;
    volatile double[] slidingWindowSmoothedSignalPhaseSpectrum;

    volatile double[] medianSmoothedSignal;
    volatile double[] medianSmoothedSignalAmplitudeSpectrum;
    volatile double[] medianSmoothedSignalPhaseSpectrum;

    volatile double[] parabolicSmoothedSignal;
    volatile double[] parabolicSmoothedSignalAmplitudeSpectrum;
    volatile double[] parabolicSmoothedSignalPhaseSpectrum;

    public
    double[] getNoisySignal()
    {
        return noisySignal.get();
    }

    public
    double[] getNoisyAmplitudeSpectrum()
    {
        return noisyAmplitudeSpectrum.get();
    }

    public
    double[] getNoisyPhaseSpectrum()
    {
        return noisyPhaseSpectrum.get();
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

    // -------------------------------------------------------------------- //

    volatile double[] lpSignal;
    volatile double[] lpAmpSpectrum;
    volatile double[] lpPhsSpectrum;

    volatile double[] hpSignal;
    volatile double[] hpAmpSpectrum;
    volatile double[] hpPhsSpectrum;

    volatile double[] bpSignal;
    volatile double[] bpAmpSpectrum;
    volatile double[] bpPhsSpectrum;

    public double[] getLPFSignal()
    {
        return lpSignal;
    }

    public double[] getLPFAmplitudeSpectrum()
    {
        return lpAmpSpectrum;
    }

    public double[] getLPFPhaseSpectrum()
    {
        return lpPhsSpectrum;
    }

    public double[] getHPFSignal()
    {
        return hpSignal;
    }

    public double[] getHPFAmplitudeSpectrum()
    {
        return hpAmpSpectrum;
    }

    public double[] getHPFPhaseSpectrum()
    {
        return hpPhsSpectrum;
    }

    public double[] getBPFSignal()
    {
        return bpSignal;
    }

    public double[] getBPFAmplitudeSpectrum()
    {
        return bpAmpSpectrum;
    }

    public double[] getBPFPhaseSpectrum()
    {
        return bpPhsSpectrum;
    }

    // -------------------------------------------------------------------- //

    Exception exception;

    public
    Exception getException()
    {
        return exception;
    }

    public
    boolean isSuccess()
    {
        return exception == null;
    }
}
