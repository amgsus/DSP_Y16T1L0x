package dsplab.logic.gen.modifier.impl;

import dsplab.logic.gen.modifier.ValueModifier;
import dsplab.logic.gen.modifier.alg.Operation;

public abstract class AbstractValueModifier implements ValueModifier
{
    public AbstractValueModifier()
    {
        this.ruleSize = 0;            // Default (0)
        this.startCoefficient = 1.00; // Default (100%)
        this.finalCoefficient = 1.00; // Default (100%)
    }

    // -------------------------------------------------------------------- //

    private int ruleSize;

    private double startCoefficient;
    private double finalCoefficient;

    // -------------------------------------------------------------------- //

    @Override
    public double[] getAllValues()
    {
        return getAllValues(getSize());
    }

    @Override
    public double[] getAllValues(int size)
    {
        return getAllValues(getSize(), getStartBound(),
            getFinalBound());
    }

    // -------------------------------------------------------------------- //

    @Override
    public int getSize()
    {
        return ruleSize;
    }

    @Override
    public double getStartBound()
    {
        return startCoefficient;
    }

    @Override
    public double getFinalBound()
    {
        return finalCoefficient;
    }

    @Override
    public void setSize(int size)
    {
        this.ruleSize = size;
    }

    @Override
    public void setStartValue(double value)
    {
        this.startCoefficient = value;
    }

    @Override
    public void setFinalValue(double value)
    {
        this.finalCoefficient = value;
    }

    @Override
    public boolean isNegativeDelta()
    {
        double delta = this.startCoefficient - this.finalCoefficient;
        return Double.compare(delta, 0.00) < 0;
    }

    @Override
    public void setBounds(double startValue, double finalValue)
    {
        this.setStartValue(startValue);
        this.setFinalValue(finalValue);
    }

    @Override
    public double applyToValue(double value, int n, int period, int periods)
    {
        return 0;
    }

    @Override
    public void setForEachPeriod(boolean forEachPeriod)
    {

    }

    @Override
    public boolean isForEachPeriod()
    {
        return false;
    }

    @Override
    public void setOperation(Operation operation)
    {

    }

    @Override
    public Operation getOperation()
    {
        return null;
    }
}
