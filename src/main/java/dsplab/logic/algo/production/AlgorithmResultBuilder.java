package dsplab.logic.algo.production;

import dsplab.architecture.Builder;
import dsplab.logic.signal.Signal;

public class AlgorithmResultBuilder implements Builder<AlgorithmResult,
    AlgorithmResultBuilder>
{
    public AlgorithmResultBuilder()
    {
        // ...
    }

    public static AlgorithmResultBuilder newInstance() { return new
        AlgorithmResultBuilder(); }

    // -------------------------------------------------------------------- //

    protected AlgorithmResult obj;

    protected void assertObjectNotNull()
    {
        if (obj == null)
            throw new IllegalStateException("Object is not initialized");
    }

    // -------------------------------------------------------------------- //

    public
    AlgorithmResultBuilder setData(double[] amplitudes)
    {
        obj.data = amplitudes;
        return this;
    }

    public
    AlgorithmResultBuilder setSignal(Signal signal)
    {
        obj.signal = signal;
        return this;
    }

    public
    AlgorithmResultBuilder setRMSByFormulaA(double[] values)
    {
        obj.rmsA = values;
        return this;
    }

    public
    AlgorithmResultBuilder setRMSByFormulaB(double[] values)
    {
        obj.rmsB = values;
        return this;
    }

    public
    AlgorithmResultBuilder setRMSAmplitudes(double[] values)
    {
        obj.rmsAmplitudes = values;
        return this;
    }

    public
    AlgorithmResultBuilder setSampleCount(int count)
    {
        obj.sampleCount = count;
        return this;
    }

    public
    AlgorithmResultBuilder setNoisySignal(double[] values)
    {
        obj.noisySignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setSliAmplitudeSpectrum(double[] values)
    {
        obj.slidingWindowSmoothedSignalAmplitudeSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setSliPhaseSpectrum(double[] values)
    {
        obj.slidingWindowSmoothedSignalPhaseSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setAmplitudeSpectrum(double[] values)
    {
        obj.amplitudeSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setPhaseSpectrum(double[] values)
    {
        obj.phaseSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setSliSignal(double[] values)
    {
        obj.slidingWindowSmoothedSignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setMdnSignal(double[] values)
    {
        obj.medianSmoothedSignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setMdnAmplitudeSpectrum(double[] values)
    {
        obj.medianSmoothedSignalAmplitudeSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setMdnPhaseSpectrum(double[] values)
    {
        obj.medianSmoothedSignalPhaseSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setPblSignal(double[] values)
    {
        obj.parabolicSmoothedSignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setPblAmplitudeSpectrum(double[] values)
    {
        obj.parabolicSmoothedSignalAmplitudeSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setPblPhaseSpectrum(double[] values)
    {
        obj.parabolicSmoothedSignalPhaseSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setNoisyAmplitudeSpectrum(double[] values)
    {
        obj.noisyAmplitudeSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setNoisyPhaseSpectrum(double[] values)
    {
        obj.noisyPhaseSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setRestoredSignal(double[] values)
    {
        obj.restoredSignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setRestoredWithPhaseSignal(double[] values)
    {
        obj.restoredWithPhaseSignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setPeriodCount(int count)
    {
        obj.periodCount = count;
        return this;
    }

    // -------------------------------------------------------------------- //

    @Override
    public
    AlgorithmResultBuilder newObject()
    {
        obj = AlgorithmResult.newInstance();
        return this;
    }

    @Override
    public
    AlgorithmResult build()
    {
        assertObjectNotNull();

        AlgorithmResult builtObj = obj;
        obj = null;
        return builtObj;
    }
}
