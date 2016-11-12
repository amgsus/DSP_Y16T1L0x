package dsplab.logic.gen.modifier.impl;

import dsplab.logic.gen.modifier.ValueModifier;

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
        return getAllValues(getSize(), getStartValue(),
            getFinalValue());
    }

    // -------------------------------------------------------------------- //

    @Override
    public int getSize()
    {
        return ruleSize;
    }

    @Override
    public double getStartValue()
    {
        return startCoefficient;
    }

    @Override
    public double getFinalValue()
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
    public void setCoefficientBounds(double startValue, double finalValue)
    {
        this.setStartValue(startValue);
        this.setFinalValue(finalValue);
    }
}
