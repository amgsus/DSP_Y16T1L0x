package dsplab.logic.filter.impl;

import dsplab.logic.filter.SignalFilter;

public class ParabolicFilter implements SignalFilter
{
    @Override
    public double[] apply(double[] signal)
    {
        if (signal == null)
            throw new IllegalArgumentException("signal:<null>");

        double fltSignal[] = new double[signal.length];

        int OFFSET = 6; // See indices below

        for (int i = OFFSET; i < signal.length - OFFSET; i++) {

            fltSignal[i] = (
                110 * signal[i - 6] -
                    198 * signal[i - 5] -
                    135 * signal[i - 4] +
                    110 * signal[i - 3] +
                    390 * signal[i - 2] +
                    600 * signal[i - 1] +
                    677 * signal[i] +
                    600 * signal[i + 1] +
                    390 * signal[i + 2] +
                    110 * signal[i + 3] -
                    135 * signal[i + 4] -
                    198 * signal[i + 5] +
                    110 * signal[i + 6]
                ) / 2431;

        }

        return fltSignal;
    }
}
