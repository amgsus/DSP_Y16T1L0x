package dsplab.logic.gen.modifier.impl.concrete;

import dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm;
import dsplab.logic.gen.modifier.impl.AbstractValueModifier;

import static dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm.LINEAR;

public class LinearValueModifier extends AbstractValueModifier
{
    @Override
    public
    ValueModifierAlgorithm getScale()
    {
        return LINEAR;
    }

    @Override
    public double[] getAllValues(int tickCount, int ticksPerPeriod)
    {
        if (tickCount < 0)
            throw new IllegalArgumentException("tickCount");
        if (ticksPerPeriod < 1)
            throw new IllegalArgumentException("ticksPerPeriod");

        double modValues[] = new double[tickCount];

        double step = (getFinalValue() - getStartValue()) / (tickCount - 1);
        double offset = 0F;

        for (int i = 0; i < tickCount; i++) {

            if (isResetOnEachPeriod()) {
                if (i % ticksPerPeriod == 0) {
                    offset = 0;
                }
            }

            modValues[i] = getStartValue() + offset;
            offset += step;
        }

        return modValues;
    }
}
