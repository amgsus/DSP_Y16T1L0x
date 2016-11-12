package dsplab.logic.ft.impl.discrete;

import dsplab.logic.MathUtils;
import dsplab.logic.ft.SinTable;
import dsplab.logic.ft.impl.AbstractFourierTransform;

public class DiscreteFourierTransform extends AbstractFourierTransform
{
    /**
     * Discrete Fourier Transform (DFT).
     *
     * @param signal    calculated signal values ('x(n)')
     * @param n         'n'
     * @param samples   'N'
     */
    double discrete(double[] signal, int n, int samples)
    {
        int M = samples % 2 == 0 ? samples / 2 : (samples - 1) / 2;

        return MathUtils.sum(0, M, R -> getRe(signal, R, samples) *
            sinTable.getCosFromTable(n * R) + getIm(signal, R, samples) *
            sinTable.getSinFromTable(n * R));
    }

    /**
     * @param signal    calculated signal values ('x(n)')
     * @param harmonic  'J'
     * @param samples   'N'
     */
    double getAmplitude(double[] signal, int harmonic, int samples)
    {
        double cosRe = getRe(signal, harmonic, samples);
        double sinIm = getIm(signal, harmonic, samples);

        return Math.sqrt(Math.pow(cosRe, 2) + Math.pow(sinIm, 2));
    }

    /**
     * @param signal    calculated signal values ('x(n)')
     * @param harmonic  'R'
     * @param samples   'N'
     */
    double getRe(double[] signal, int harmonic, int samples)
    {
        return MathUtils.sum(0, samples - 1, i ->
            signal[i] * sinTable.getCosFromTable(i * harmonic)) * 2 / samples;
    }

    /**
     * @param signal    calculated signal values ('x(n)')
     * @param harmonic  'R'
     * @param samples   'N'
     */
    double getIm(double[] signal, int harmonic, int samples)
    {
        return MathUtils.sum(0, samples - 1, i ->
            signal[i] * sinTable.getSinFromTable(i * harmonic)) * 2 / samples;
    }

    @Override
    public double calculateAmplitude()
    {
        final int J = 1;
        final int N = this.range;

        if (N == 0)
            return 0.0;

        return getAmplitude(this.spectrum, J, N);
    }

    @Override
    public double[] calculateAmplitudeSpectrum()
    {
        int size = range / 2;
        double a[] = new double[size];

        for (int h = 0; h < a.length; h++) {
            // a[h] = discrete(this.spectrum, h, this.range);
            a[h] = getAmplitude(this.spectrum, h, this.range);
        }

        return a;
    }

    @Override
    public double[] calculatePhaseSpectrum()
    {
        int size = range / 2;
        double p[] = new double[size];

        for (int n = 0; n < p.length; n++) {
            p[n] = Math.atan(getIm(this.spectrum, n, this.range) /
                getRe(this.spectrum, n, this.range));
        }

        return p;
    }
}
