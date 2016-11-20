package dsplab.logic.gen.modifier;

import dsplab.logic.gen.modifier.alg.Operation;
import dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm;

public interface ValueModifier
{
    void setSize(int size);

    void setStartValue(double value);
    void setFinalValue(double value);

    int getSize();
    double getStartBound();
    double getFinalBound();

    void setBounds(double startValue, double finalValue);

    double[] getAllValues();
    double[] getAllValues(int size);
    double[] getAllValues(int size, double startValue, double finalValue);

    boolean isNegativeDelta();

    /* Confirmed */

    Operation getOperation();
    void setOperation(Operation operation);

    ValueModifierAlgorithm getScale();

    boolean isForEachPeriod();
    void setForEachPeriod(boolean forEachPeriod);

    /* Confirmed (used by a generator) */

    double applyToValue(double value, int n, int period, int periods);
}
