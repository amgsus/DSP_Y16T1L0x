package dsplab.logic.filter.fa;

import dsplab.logic.filter.SignalFilter;
import dsplab.logic.filter.alg.FilterAlgorithm;
import dsplab.logic.filter.impl.MedianFilter;
import dsplab.logic.filter.impl.ParabolicFilter;
import dsplab.logic.filter.impl.SlidingFilter;

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
