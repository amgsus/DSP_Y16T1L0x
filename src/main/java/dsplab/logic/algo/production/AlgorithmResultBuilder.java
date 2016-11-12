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
    AlgorithmResultBuilder setAmplitudes(double[] amplitudes)
    {
        obj.amplitudes = amplitudes;
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
    AlgorithmResultBuilder setFtAmplitudes(double[] values)
    {
        obj.ftAmplitudes = values;
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
    AlgorithmResultBuilder setSliSignalAmplitudeSpectrum(double[] values)
    {
        obj.slidingWindowSmoothedSignalAmplitudeSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setSliSignalPhaseSpectrum(double[] values)
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
