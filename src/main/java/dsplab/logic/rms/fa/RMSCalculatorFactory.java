package dsplab.logic.rms.fa;

import dsplab.logic.rms.RMSCalculator;
import dsplab.logic.rms.alg.RMSFormula;
import dsplab.logic.rms.impl.concrete.RMSCalculatorImplA;
import dsplab.logic.rms.impl.concrete.RMSCalculatorImplB;

public final class RMSCalculatorFactory
{
    private RMSCalculatorFactory() {}
    private static RMSCalculatorFactory factory = new RMSCalculatorFactory();

    public  static RMSCalculatorFactory getFactory() { return factory; }

    // -------------------------------------------------------------------- //

    @SuppressWarnings("unchecked")
    public <T extends RMSCalculator> T giveMeSomethingLike(RMSFormula formula)
        throws IllegalArgumentException
    {
        switch (formula) {
            case A:
                return (T) new RMSCalculatorImplA();
            case B:
                return (T) new RMSCalculatorImplB();

            default:
                final String s = "'%s' is not supported by the factory";
                throw new IllegalArgumentException(String.format(s, formula));
        }
    }
}
