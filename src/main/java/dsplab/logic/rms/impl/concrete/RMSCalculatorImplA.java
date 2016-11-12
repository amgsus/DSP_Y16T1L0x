package dsplab.logic.rms.impl.concrete;

import dsplab.logic.rms.impl.AbstractRMSCalculator;

public class RMSCalculatorImplA extends AbstractRMSCalculator
{
    @Override
    public double calculateRMS()
    {
        return Math.sqrt(this.calculateCommon());
    }
}
