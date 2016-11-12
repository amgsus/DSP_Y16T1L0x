package dsplab.logic.rms.impl;

import dsplab.logic.MathUtils;
import dsplab.logic.rms.RMSCalculator;

public abstract class AbstractRMSCalculator implements RMSCalculator
{
    protected int range;
    protected double[] spectrum;

    protected double calculateCommon()
    {
        int mBolshoe = this.range;
        return MathUtils.sum(0, mBolshoe, i -> Math.pow(spectrum[i], 2)) /
            (1 + mBolshoe);
    }

    // -------------------------------------------------------------------- //

    @Override
    public void setSpectrum(double[] spectrum)
    {
        this.spectrum = spectrum;
        this.range = Math.min(this.spectrum.length, this.range);
    }

    @Override
    public void setRange(int range)
    {
        if (range < 0)
            throw new IllegalArgumentException("range less than ZERO");

        if (this.spectrum != null) {
            if (this.spectrum.length < this.range)
                throw new IllegalArgumentException("range out of bounds");
        }

        this.range = range;
    }
}
