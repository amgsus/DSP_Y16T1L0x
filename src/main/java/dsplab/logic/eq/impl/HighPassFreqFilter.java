package dsplab.logic.eq.impl;

import dsplab.logic.eq.BandPassFilter;

public class HighPassFreqFilter implements BandPassFilter
{
    public static HighPassFreqFilter newInstance() { return new
        HighPassFreqFilter(); }

    @Override
    public double[] apply(double[] spectrum)
    {
        if (spectrum == null)
            throw new IllegalArgumentException("spectrum:null");

        int lo = spectrum.length * 2 / 3;
        int hi = spectrum.length - 1;

        return BandPassFilter.passFreq(spectrum, lo, hi);
    }
}
