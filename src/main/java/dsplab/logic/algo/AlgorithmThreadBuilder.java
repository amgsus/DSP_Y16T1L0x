package dsplab.logic.algo;

import dsplab.architecture.Builder;
import dsplab.architecture.callback.Delegate;
import dsplab.logic.algo.fa.AlgorithmThreadFactory;
import dsplab.logic.gen.alg.GenID;
import dsplab.logic.signal.Signal;

import java.util.List;

public final class AlgorithmThreadBuilder implements
    Builder<AlgorithmThread, AlgorithmThreadBuilder>
{
    public AlgorithmThreadBuilder()
    {
        // ...
    }

    public static AlgorithmThreadBuilder newInstance() { return
        new AlgorithmThreadBuilder(); }

    // -------------------------------------------------------------------- //

    protected AlgorithmThread thread = null;

    // -------------------------------------------------------------------- //

    protected void assertObjectNotNull()
    {
        if (thread == null)
            throw new IllegalStateException("Object is not initialized");
    }

    // -------------------------------------------------------------------- //

    public
    AlgorithmThreadBuilder setSignalList(List<Signal> signals)
    {
        assertObjectNotNull();
        thread.setSignalList(signals);

        return this;
    }

    public
    AlgorithmThreadBuilder setOnStart(Delegate delegate)
    {
        assertObjectNotNull();
        thread.setOnStart(delegate);

        return this;
    }

    public
    AlgorithmThreadBuilder setOnSuccess(Delegate delegate)
    {
        assertObjectNotNull();
        thread.setOnSuccess(delegate);

        return this;
    }

    public
    AlgorithmThreadBuilder setSampleCount(int samples)
    {
        assertObjectNotNull();
        thread.setSampleCount(samples);

        return this;
    }

    public
    AlgorithmThreadBuilder setPeriodCount(int periods)
    {
        assertObjectNotNull();
        thread.setPeriodCount(periods);

        return this;
    }

    public
    AlgorithmThreadBuilder setGenerator(GenID id)
    {
        assertObjectNotNull();
        thread.setGenerator(id);

        return this;
    }

    public
    AlgorithmThreadBuilder setExtendedCalculations(boolean enabled)
    {
        thread.setExtendedCalculationEnabled(enabled);
        return this;
    }

    // -------------------------------------------------------------------- //

    @Override
    public AlgorithmThreadBuilder newObject()
    {
        thread = AlgorithmThreadFactory.getFactory().giveMeDefault();
        return this;
    }

    @Override
    public AlgorithmThread build()
    {
        assertObjectNotNull();

        AlgorithmThread builtThread = thread;
        thread = null;

        return builtThread;
    }
}
