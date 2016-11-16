package dsplab.logic.ft;

import dsplab.logic.MathUtils;

import java.util.Arrays;

public class SignalRestorer
{
    public SignalRestorer()
    {
        this(null, null);
    }

    public SignalRestorer(double[] amplitudeSpectrum, double[] phaseSpectrum)
    {
        setAmplitudes(amplitudeSpectrum);
        setPhases(phaseSpectrum);

        if (amplitudeSpectrum != null) {
            if (phaseSpectrum != null) {
                samples = Math.min(amplitudeSpectrum.length,
                    phaseSpectrum.length);
            } else {
                samples = amplitudeSpectrum.length;
            }
        }
    }

    public static SignalRestorer newInstance() { return new SignalRestorer(); }

    // -------------------------------------------------------------------- //

    protected double[] amplitudes = null;
    protected double[] phases = null;
    protected double gain = 0; // A0
    protected int samples = 0;

    // -------------------------------------------------------------------- //

    public
    double[] restore()
    {
        return restoreSignal(amplitudes, phases, gain, samples);
    }

    public
    double[] restoreWithoutPhase()
    {
        double[] zeroPhases = new double[samples];
        Arrays.fill(zeroPhases, 0);

        return restoreSignal(amplitudes, zeroPhases, gain, samples);
    }

    public static final String ERR_AMPLITUDE_LENGTH =
        "The length of <amplitudes> less than <sampleCount>";
    public static final String ERR_PHASE_LENGTH =
        "The length of <amplitudes> less than <sampleCount>";
    public static final String ERR_LENGTH_NOT_EQUAL =
        "The length of <amplitudes> is not equal to <phases> one";
    public static final String ERR_SAMPLE_COUNT =
        "Sample count must be a positive number [2^x], x > 0";

    public static double[] restoreSignal(double[] amps, double[] phases,
        double gain, int sampleCount) throws IllegalArgumentException
    {
        if (amps == null)
            throw new IllegalArgumentException("amps:<null>");
        if (phases == null)
            throw new IllegalArgumentException("phases:<null>");
        if (sampleCount < 1)
            throw new IllegalArgumentException(ERR_SAMPLE_COUNT);
        if (amps.length < sampleCount)
            throw new IllegalArgumentException(ERR_AMPLITUDE_LENGTH);
        if (phases.length < sampleCount)
            throw new IllegalArgumentException(ERR_PHASE_LENGTH);
        if (phases.length != amps.length)
            throw new IllegalArgumentException(ERR_LENGTH_NOT_EQUAL);

        /*
        double[] signal = new double[sampleCount * 2];
        Arrays.fill(signal, 0);

        final int sumTo = sampleCount / 2 - 1;
        final double const2PiN = 2 * Math.PI / sampleCount;

        gain /= 2;

        for (int i = 0; i < signal.length; i++) {
            double _2PiNi = const2PiN * i;
            final int _i_ = i;

            signal[i] = gain + MathUtils.sum(1, sumTo, j -> {
                if (_i_ < signal.length) {
                    return amps[j] * Math.cos(_2PiNi * j - phases[j]);
                } else {
                    int newJ = signal.length - (j % signal.length);
                    return amps[newJ] * Math.cos(_2PiNi * newJ - phases[newJ]);
                }
            });
        }

        return signal;
        */

        double[] signal = new double[sampleCount];
        Arrays.fill(signal, 0);

        final int sumTo = sampleCount / 2 - 1;
        final double const2PiN = 2 * Math.PI / sampleCount;

        gain /= 2;

        for (int i = 0; i < signal.length; i++) {
            double _2PiNi = const2PiN * i;
            signal[i] = gain + MathUtils.sum(1, sumTo, j ->
                amps[j] * Math.cos(_2PiNi * j - phases[j]));
        }

        return signal;
    }

    // -------------------------------------------------------------------- //

    public
    void setAmplitudes(double[] values)
    {
        amplitudes = values;
    }

    public
    void setPhases(double[] values)
    {
        phases = values;
    }

    public
    void setGain(double gain)
    {
        this.gain = gain;
    }

    public
    void setSampleCount(int samples)
    {
        this.samples = samples;
    }
}
