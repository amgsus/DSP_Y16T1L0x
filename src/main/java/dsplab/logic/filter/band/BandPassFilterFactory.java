package dsplab.logic.filter.band;

import dsplab.logic.filter.band.alg.FilterType;
import dsplab.logic.filter.band.impl.HighPassFreqFilter;
import dsplab.logic.filter.band.impl.LowPassFreqFilter;
import dsplab.logic.filter.band.impl.MiddlePassFreqFilter;

import static dsplab.common.Const.MSG_FACTORY_UNSUPPORTED;

public final class BandPassFilterFactory
{
    private BandPassFilterFactory() {}

    private static BandPassFilterFactory factory =
        new BandPassFilterFactory();
    public  static BandPassFilterFactory getFactory() { return factory; }

    @SuppressWarnings("unchecked")
    public <T extends BandPassFilter> T giveMe(FilterType type)
        throws IllegalArgumentException
    {
        switch (type) {
            case BAND:
                return (T) MiddlePassFreqFilter.newInstance();
            case LOWPASS:
                return (T) LowPassFreqFilter.newInstance();
            case HIGHPASS:
                return (T) HighPassFreqFilter.newInstance();

            default:
                String s = String.format(MSG_FACTORY_UNSUPPORTED, type);
                throw new IllegalArgumentException(s);
        }
    }
}
