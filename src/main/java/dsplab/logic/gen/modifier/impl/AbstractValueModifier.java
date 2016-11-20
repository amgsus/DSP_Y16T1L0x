package dsplab.logic.gen.modifier.impl;

import dsplab.logic.gen.modifier.ValueModifier;
import dsplab.logic.gen.modifier.alg.Operation;

public abstract class AbstractValueModifier implements ValueModifier
{
    private double startValue = 1.0;
    private double finalValue = 1.0;

    @Override
    public double getStartValue()
    {
        return startValue;
    }

    @Override
    public double getFinalValue()
    {
        return finalValue;
    }

    @Override
    public
    void setStartValue(double value)
    {
        this.startValue = value;
    }

    @Override
    public
    void setFinalValue(double value)
    {
        this.finalValue = value;
    }

    // -------------------------------------------------------------------- //

    private Operation operation = Operation.MULTIPLICATION;

    @Override
    public Operation getOperation()
    {
        return operation;
    }

    @Override
    public
    void setOperation(Operation operation)
    {
        if (operation == null)
            throw new IllegalArgumentException("null");

        this.operation = operation;
    }

    // -------------------------------------------------------------------- //

    private boolean resetOnEachPeriod = false;

    @Override
    public boolean isResetOnEachPeriod()
    {
        return resetOnEachPeriod;
    }

    @Override
    public
    void setResetOnEachPeriod(boolean resetOnEachPeriod)
    {
        this.resetOnEachPeriod = resetOnEachPeriod;
    }

    // -------------------------------------------------------------------- //

    @Override
    public void setBounds(double startValue, double finalValue)
    {
        setStartValue(startValue);
        setFinalValue(finalValue);
    }

    // -------------------------------------------------------------------- //

    @Override
    public double[] getAllValues(int tickCount)
    {
        return getAllValues(tickCount, tickCount);
    }
}
