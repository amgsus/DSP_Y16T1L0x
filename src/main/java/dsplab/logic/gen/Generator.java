package dsplab.logic.gen;

import dsplab.logic.function.Function;
import dsplab.logic.function.fa.Functions;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.Signal;

public interface Generator
{
    void setSignal(Signal signal);
    void setSampleCount(int samples);
    void setPeriodCount(int periods);
    double[] run();

    // -------------------------------------------------------------------- //

    static double calculateMomentaryAmplitude(Harmonic harmonic,
        double offset, double sampleCount)
    {
        Function f = Functions.getFactory()
            .getFunction(harmonic.getWaveform());

        return f.calculate(harmonic, offset, sampleCount);
    }

    static double calculateMomentaryAmplitude(Signal signal,
        double offset, double sampleCount)
    {
        double mA = 0;

        for (Harmonic harmonic : signal.getHarmonics())
            mA += calculateMomentaryAmplitude(harmonic, offset, sampleCount);

        return mA;
    }
}
