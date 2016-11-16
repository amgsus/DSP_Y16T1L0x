package dsplab.logic.eq.impl;

import dsplab.logic.eq.BandPassFilter;

public class LowPassFreqFilter implements BandPassFilter
{
    public static LowPassFreqFilter newInstance() { return new
        LowPassFreqFilter(); }

    @Override
    public double[] apply(double[] spectrum)
    {
        if (spectrum == null)
            throw new IllegalArgumentException("spectrum:null");

        int lo = 0;
        int hi = spectrum.length / 3;

        return BandPassFilter.passFreq(spectrum, lo, hi);
    }
}
