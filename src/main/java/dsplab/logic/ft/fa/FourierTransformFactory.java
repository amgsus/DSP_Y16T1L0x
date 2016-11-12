package dsplab.logic.ft.fa;

import dsplab.logic.ft.FourierTransform;
import dsplab.logic.ft.alg.FFTImpl;
import dsplab.logic.ft.impl.discrete.DiscreteFourierTransform;
import dsplab.logic.ft.impl.fast.FastFourierTransform;

public final class FourierTransformFactory
{
    FourierTransformFactory() {}
    private static FourierTransformFactory factory =
        new FourierTransformFactory();

    public  static FourierTransformFactory getFactory() { return factory; }

    // -------------------------------------------------------------------- //

    @SuppressWarnings("unchecked")
    public <T extends FourierTransform> T newFFTImplementation(FFTImpl impl)
    {
        switch (impl) {
            case DISCRETE:
                return (T) new DiscreteFourierTransform();
            case FAST:
                return (T) new FastFourierTransform();

            default:
                final String s = "'%s' is not supported by the factory";
                throw new IllegalArgumentException(String.format(s, impl));
        }
    }
}
