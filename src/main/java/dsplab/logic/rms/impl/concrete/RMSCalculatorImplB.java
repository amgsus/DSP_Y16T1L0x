package dsplab.logic.rms.impl.concrete;

import dsplab.logic.MathUtils;
import dsplab.logic.rms.impl.AbstractRMSCalculator;

public class RMSCalculatorImplB extends AbstractRMSCalculator
{
    @Override
    public double calculateRMS()
    {
        int mBolshoe = this.range;
        return Math.sqrt(this.calculateCommon() -
            Math.pow(MathUtils.sum(0, mBolshoe, i -> this.spectrum[i]) /
                (mBolshoe + 1), 2));
    }
}
