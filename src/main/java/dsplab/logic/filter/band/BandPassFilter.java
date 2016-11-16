package dsplab.logic.filter.band;

import java.util.Arrays;

public interface BandPassFilter
{
    double[] apply(double[] spectrum);

    // -------------------------------------------------------------------- //

    String MSG_INVALID_LOBOUND
        = "Lo-bound must be not less than zero";
    String MSG_INVALID_HIBOUND
        = "Hi-bound must be not less than lo-bound";

    static double[] passFreq(double[] spectrum, int loBound, int hiBound)
        throws IllegalArgumentException
    {
        if (spectrum == null)
            throw new IllegalArgumentException("spectrum:null");
        if (loBound < 0)
            throw new IllegalArgumentException(MSG_INVALID_LOBOUND);
        if (hiBound < loBound)
            throw new IllegalArgumentException(MSG_INVALID_HIBOUND);

        double fltSpectrum[] = new double[spectrum.length];
        Arrays.fill(fltSpectrum, 0);

        hiBound = Math.min(hiBound, spectrum.length);

        if (loBound < spectrum.length) {
            System.arraycopy(spectrum, loBound, fltSpectrum, loBound,
                hiBound - loBound + 1);
        }

        return fltSpectrum;
    }
}
