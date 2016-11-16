package dsplab.logic.eq;

import dsplab.logic.eq.alg.FilterType;
import dsplab.logic.eq.impl.HighPassFreqFilter;
import dsplab.logic.eq.impl.LowPassFreqFilter;
import dsplab.logic.eq.impl.MiddlePassFreqFilter;

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
