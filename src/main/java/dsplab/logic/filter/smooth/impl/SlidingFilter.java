package dsplab.logic.filter.smooth.impl;

import dsplab.logic.MathUtils;
import dsplab.logic.filter.smooth.SignalFilter;

public class SlidingFilter implements SignalFilter
{
    static final String MUST_ODD = "Value must be an odd integer";
    static final String MUST_POSITIVE = "Value must be a positive integer";

    // Variant: 2 ~ K=5
    public SlidingFilter()
    {
        this.setK(5);
    }

    int windowSize;
    int constK;

    public
    void setK(int value)
    {
        if (value < 1)
            throw new IllegalArgumentException(MUST_POSITIVE);
        if (value % 2 != 1)
            throw new IllegalArgumentException(MUST_ODD);

        this.constK = value;
        this.windowSize = (value - 1) / 2;
    }

    public
    void setWindowSize(int value)
    {
        if (value < 0)
            throw new IllegalArgumentException(MUST_POSITIVE);
        if (value % 2 != 1)
            throw new IllegalArgumentException(MUST_ODD);

        this.windowSize = value;
        this.constK = value * 2 + 1;
    }

    @Override
    public double[] apply(double[] signal)
    {
        if (signal == null)
            throw new IllegalArgumentException("signal:<null>");

        int length = signal.length;
        int w = this.windowSize;

        double fltSignal[] = new double[length];

        for (int i = 0; i < length - w; i++) {
            double sum = MathUtils.sum(i, i + w, j -> signal[j]);
            fltSignal[i + w / 2] = sum / this.windowSize;
        }

        return fltSignal;
    }
}
