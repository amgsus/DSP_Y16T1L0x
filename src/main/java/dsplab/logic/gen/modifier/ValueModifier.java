package dsplab.logic.gen.modifier;

import dsplab.logic.gen.modifier.alg.Operation;
import dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm;

public interface ValueModifier
{
    /* Confirmed */

    double getStartValue();
    double getFinalValue();

    void setStartValue(double value);
    void setFinalValue(double value);

    Operation getOperation();
    void setOperation(Operation operation);

    boolean isResetOnEachPeriod();
    void setResetOnEachPeriod(boolean resetOnEachPeriod);

    void setBounds(double startValue, double finalValue);

    /* This methods should be overriden by final implementation */

    ValueModifierAlgorithm getScale();

    /* This methids call by a generator */

    double[] getAllValues(int tickCount);
    double[] getAllValues(int tickCount, int ticksPerPeriod);
}
