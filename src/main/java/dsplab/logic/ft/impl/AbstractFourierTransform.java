package dsplab.logic.ft.impl;

import dsplab.logic.ft.FourierTransform;
import dsplab.logic.ft.SinTable;

public abstract class AbstractFourierTransform implements FourierTransform
{
    protected double[] spectrum;
    protected int range;
    protected SinTable sinTable = new SinTable();

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
        this.sinTable.init(range, 1.0);
    }
}
