package dsplab.logic.gen.modifier.impl.concrete;

import dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm;
import dsplab.logic.gen.modifier.impl.AbstractValueModifier;

import static dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm.LOGARITHMIC;

public class LogarithmicValueModifier extends AbstractValueModifier
{
    public LogarithmicValueModifier() {}

    // -------------------------------------------------------------------- //

    @Override
    public double[] getAllValues(int size, double startValue,
        double finalValue)
    {
        double[] c = new double[size];

        /*
        final double step = (startCoefficient + finalCoefficient) / size;
        double offset = 0; // Replace multiplication with sum

        for (int i = 0; i < c.length; i++) {
            c[i] = startCoefficient + offset;
            offset = offset + step;
        }
        */

        return c;
    }

    @Override
    public ValueModifierAlgorithm getScale()
    {
        return LOGARITHMIC;
    }
}
