package dsplab.logic.gen.modifier.impl.concrete;

import dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm;
import dsplab.logic.gen.modifier.impl.AbstractValueModifier;

import static dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm.LOGARITHMIC;

public class LogarithmicValueModifier extends AbstractValueModifier
{
    @Override
    public ValueModifierAlgorithm getScale()
    {
        return LOGARITHMIC;
    }

    @Override
    public double[] getAllValues(int tickCount, int ticksPerPeriod)
    {
        throw new RuntimeException("Not implemented");
    }
}
