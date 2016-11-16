package dsplab.logic.filter.smooth.fa;

import dsplab.logic.filter.smooth.SignalFilter;
import dsplab.logic.filter.smooth.alg.FilterAlgorithm;
import dsplab.logic.filter.smooth.impl.MedianFilter;
import dsplab.logic.filter.smooth.impl.ParabolicFilter;
import dsplab.logic.filter.smooth.impl.SlidingFilter;

public final class SignalFilterFactory
{
    SignalFilterFactory() {}

    private static final SignalFilterFactory factory
        = new SignalFilterFactory();
    public  static SignalFilterFactory getFactory() { return factory; }

    // -------------------------------------------------------------------- //

    @SuppressWarnings("unchecked")
    public <T extends SignalFilter> T newFilter(FilterAlgorithm algo)
    {
        switch (algo) {
            case SLIDING:
                return (T) new SlidingFilter();
            case MEDIAN:
                return (T) new MedianFilter();
            case PARABOLIC:
                return (T) new ParabolicFilter();

            default:
                final String s = "'%s' is not supported by the factory";
                throw new IllegalArgumentException(String.format(s, algo));
        }
    }
}
