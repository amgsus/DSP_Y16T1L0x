package dsplab.logic.algo;

import dsplab.architecture.callback.Delegate;
import dsplab.architecture.pattern.BuilderEx;
import dsplab.logic.algo.fa.AlgorithmThreadFactory;
import dsplab.logic.gen.alg.GenID;
import dsplab.logic.gen.modifier.ValueModifier;
import dsplab.logic.signal.Signal;
import javafx.application.Platform;

import java.util.List;

public final class AlgorithmThreadBuilder extends BuilderEx<AlgorithmThread>
{
    public AlgorithmThreadBuilder()
    {
        super(AlgorithmThreadFactory.getFactory().giveMeDefault());
    }

    public static AlgorithmThreadBuilder newInstance() { return
        new AlgorithmThreadBuilder(); }

    // -------------------------------------------------------------------- //

    public
    AlgorithmThreadBuilder setSignalList(List<Signal> signals)
    {
        getObject().setSignalList(signals);
        return this;
    }

    public
    AlgorithmThreadBuilder setSampleCount(int samples)
    {
        getObject().setSampleCount(samples);
        return this;
    }

    public
    AlgorithmThreadBuilder setPeriodCount(int periods)
    {
        getObject().setPeriodCount(periods);
        return this;
    }

    public
    AlgorithmThreadBuilder setGeneratorID(GenID gid)
    {
        getObject().setGeneratorID(gid);
        return this;
    }

    public
    AlgorithmThreadBuilder setMathEx(boolean enabled)
    {
        getObject().setExtendedCalculationEnabled(enabled);
        return this;
    }

    public
    AlgorithmThreadBuilder setAmpModifier(ValueModifier vm)
    {
        getObject().setAmplitudeModifier(vm);
        return this;
    }

    public
    AlgorithmThreadBuilder setPhsModifier(ValueModifier vm)
    {
        getObject().setPhaseModifier(vm);
        return this;
    }

    public
    AlgorithmThreadBuilder setFrqModifier(ValueModifier vm)
    {
        getObject().setFrequencyModifier(vm);
        return this;
    }

    /**
     * <b>Note:</b> A specified delegate is called on the thread that is
     * invoked {@link Thread#start}.
     */
    public
    AlgorithmThreadBuilder setOnBeforeStart(Delegate delegate)
    {
        getObject().setOnBeforeStart(delegate);
        return this;
    }

    /**
     * <b>Note:</b> A specified delegate is called on the JavaFX thread.
     * @see Platform#runLater(Runnable)
     */
    public
    AlgorithmThreadBuilder setOnDone(Delegate delegate)
    {
        getObject().setOnSuccess(delegate);
        return this;
    }
}
