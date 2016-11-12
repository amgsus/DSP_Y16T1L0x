package dsplab.logic.filter.impl;

import dsplab.logic.MathUtils;
import dsplab.logic.filter.SignalFilter;

public class SlidingFilter implements SignalFilter
{
    private int windowSize = 16;

    public void setWindowSize(int size)
    {
        this.windowSize = size;
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
            fltSignal[i + w / 2] = sum / w;
        }

        return fltSignal;
    }
}
