package dsplab.logic.ft.impl.fast;

import dsplab.logic.ft.impl.AbstractFourierTransform;

public class FastFourierTransform extends AbstractFourierTransform
{
    @Override
    public double calculateAmplitude()
    {
        return 0;
    }

    @Override
    public double[] calculateAmplitudeSpectrum()
    {
        return new double[this.range];
    }

    @Override
    public double[] calculatePhaseSpectrum()
    {
        return new double[this.range];
    }
}
