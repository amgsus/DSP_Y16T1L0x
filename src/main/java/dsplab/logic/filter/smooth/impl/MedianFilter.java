package dsplab.logic.filter.smooth.impl;

import dsplab.logic.filter.smooth.SignalFilter;

import java.util.stream.DoubleStream;

public class MedianFilter implements SignalFilter
{
    static final String MUST_ODD = "Value must be an odd integer";
    static final String MUST_POSITIVE = "Value must be a positive integer";

    // Variant: 2 ~ N=7
    public MedianFilter()
    {
        this.setWindowSize(7);
    }

    int windowSize;

    public
    void setWindowSize(int value)
    {
        if (value < 1)
            throw new IllegalArgumentException(MUST_POSITIVE);
        if (value % 2 != 1)
            throw new IllegalArgumentException(MUST_ODD);

        this.windowSize = value;
    }

    @Override
    public double[] apply(double[] signal)
    {
        if (signal == null)
            throw new IllegalArgumentException("signal:<null>");

        int length = signal.length;
        int wS = this.windowSize;

        double fltSignal[] = new double[length];

        for (int i = 0; i < length - wS; i++) {

            int skipCnt = i == 0 ? 0 : i - 1;

            double[] srt = DoubleStream.of(signal).skip(skipCnt)
                .limit(wS).sorted().toArray();

            fltSignal[i + wS] = srt[wS / 2 - 1];
        }

        return fltSignal;
    }
}
