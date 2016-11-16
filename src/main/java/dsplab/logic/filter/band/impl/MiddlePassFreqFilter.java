package dsplab.logic.filter.band.impl;

import dsplab.logic.filter.band.BandPassFilter;

public class MiddlePassFreqFilter implements BandPassFilter
{
    public static MiddlePassFreqFilter newInstance() { return new
        MiddlePassFreqFilter(); }

    @Override
    public double[] apply(double[] spectrum)
    {
        if (spectrum == null)
            throw new IllegalArgumentException("spectrum:null");

        int lo = spectrum.length / 3;
        int hi = spectrum.length * 2 / 3;

        return BandPassFilter.passFreq(spectrum, lo, hi);
    }
}
