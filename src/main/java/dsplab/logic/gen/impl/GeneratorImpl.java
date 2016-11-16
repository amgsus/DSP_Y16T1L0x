package dsplab.logic.gen.impl;

import dsplab.logic.function.fa.CompositeFunctionFactory;
import dsplab.logic.function.fa.FunctionFactory;
import dsplab.logic.gen.Generator;
import dsplab.logic.signal.Signal;

public class GeneratorImpl implements Generator
{
    public GeneratorImpl()
    {
        this.sampleCount = 512; // Default
        this.signal = null;
    }

    // -------------------------------------------------------------------- //

    protected int periodCount;
    protected int sampleCount;
    protected Signal signal;

    // -------------------------------------------------------------------- //

    @Override
    public double[] run()
    {
        double[] a = new double[this.sampleCount * this.periodCount];

        FunctionFactory ff = new CompositeFunctionFactory(this.sampleCount);

        for (int offsetX = 0; offsetX < a.length; offsetX++) {
            a[offsetX] = Generator.calculateMomentaryAmplitude(signal, offsetX,
                this.sampleCount, ff);
        }

        return a;
    }

    // -------------------------------------------------------------------- //

    @Override
    public void setSignal(Signal signal)
    {
        if (signal == null)
            throw new IllegalArgumentException("signal is null");

        this.signal = signal;
    }

    @Override
    public void setSampleCount(int samples)
    {
        if (samples < 2)
            throw new IllegalArgumentException("samples less than 2^1");

        this.sampleCount = samples;
    }

    @Override
    public void setPeriodCount(int periods)
    {
        if (periods < 1)
            throw new IllegalArgumentException("Period count less than ONE");

        this.periodCount = periods;
    }
}
