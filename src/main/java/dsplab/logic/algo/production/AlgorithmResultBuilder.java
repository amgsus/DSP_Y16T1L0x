package dsplab.logic.algo.production;

import dsplab.architecture.pattern.BuilderEx;
import dsplab.logic.signal.Signal;

public class AlgorithmResultBuilder extends BuilderEx<AlgorithmResult>
{
    public AlgorithmResultBuilder()
    {
        super(AlgorithmResult.newInstance());
    }

    public AlgorithmResultBuilder(AlgorithmResult existingObject)
    {
        super(existingObject);
    }

    public static AlgorithmResultBuilder newInstance() { return new
        AlgorithmResultBuilder(); }

    public static AlgorithmResultBuilder instanceFor(AlgorithmResult
        algoResult) { return new AlgorithmResultBuilder(algoResult); }

    // -------------------------------------------------------------------- //

    public
    AlgorithmResultBuilder setData(double[] amplitudes)
    {
        getObject().data = amplitudes;
        return this;
    }

    public
    AlgorithmResultBuilder setSignal(Signal signal)
    {
        getObject().signal = signal;
        return this;
    }

    public
    AlgorithmResultBuilder setRMSByFormulaA(double[] values)
    {
        getObject().rmsA = values;
        return this;
    }

    public
    AlgorithmResultBuilder setRMSByFormulaB(double[] values)
    {
        getObject().rmsB = values;
        return this;
    }

    public
    AlgorithmResultBuilder setAeData(double[] values)
    {
        getObject().rmsAmplitudes = values;
        return this;
    }

    public
    AlgorithmResultBuilder setSampleCount(int count)
    {
        getObject().sampleCount = count;
        return this;
    }

    public
    AlgorithmResultBuilder setNoisySignal(double[] values)
    {
        getObject().noisySignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setSliAmplitudeSpectrum(double[] values)
    {
        getObject().slidingWindowSmoothedSignalAmplitudeSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setSliPhaseSpectrum(double[] values)
    {
        getObject().slidingWindowSmoothedSignalPhaseSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setAmplitudeSpectrum(double[] values)
    {
        getObject().amplitudeSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setPhaseSpectrum(double[] values)
    {
        getObject().phaseSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setSliSignal(double[] values)
    {
        getObject().slidingWindowSmoothedSignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setMdnSignal(double[] values)
    {
        getObject().medianSmoothedSignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setMdnAmplitudeSpectrum(double[] values)
    {
        getObject().medianSmoothedSignalAmplitudeSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setMdnPhaseSpectrum(double[] values)
    {
        getObject().medianSmoothedSignalPhaseSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setPblSignal(double[] values)
    {
        getObject().parabolicSmoothedSignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setPblAmplitudeSpectrum(double[] values)
    {
        getObject().parabolicSmoothedSignalAmplitudeSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setPblPhaseSpectrum(double[] values)
    {
        getObject().parabolicSmoothedSignalPhaseSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setNoisyAmplitudeSpectrum(double[] values)
    {
        getObject().noisyAmplitudeSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setNoisyPhaseSpectrum(double[] values)
    {
        getObject().noisyPhaseSpectrum = values;
        return this;
    }

    public
    AlgorithmResultBuilder setRestoredWithoutPhaseSignal(double[] values)
    {
        getObject().restoredSignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setRestoredSignal(double[] values)
    {
        getObject().restoredWithPhaseSignal = values;
        return this;
    }

    public
    AlgorithmResultBuilder setPeriodCount(int count)
    {
        getObject().periodCount = count;
        return this;
    }
}
