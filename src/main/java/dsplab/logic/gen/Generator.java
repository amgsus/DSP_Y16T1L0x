package dsplab.logic.gen;

import dsplab.logic.function.Function;
import dsplab.logic.function.fa.FunctionFactory;
import dsplab.logic.function.fa.Functions;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.Signal;

public interface Generator
{
    void setSignal(Signal signal);
    void setSampleCount(int samples);
    void setPeriodCount(int periods);
    double[] run() throws Exception;

    void setOffset(int offset);

    // -------------------------------------------------------------------- //

    static double calculateMomentaryAmplitude(Harmonic harmonic,
        double offset, double sampleCount, FunctionFactory ff)
    {
        Function f = ff.getFunction(harmonic.getWaveform());

        return f.calculate(harmonic, offset, sampleCount);
    }

    static double calculateMomentaryAmplitude(Harmonic harmonic,
        double offset, double sampleCount)
    {
        FunctionFactory ff = Functions.getFactory();
        return calculateMomentaryAmplitude(harmonic, offset, sampleCount, ff);
    }

    static double calculateMomentaryAmplitude(Signal signal, double offset,
        double sampleCount, FunctionFactory ff)
    {
        double mA = 0;

        for (Harmonic harmonic : signal.getHarmonics())
            mA += calculateMomentaryAmplitude(harmonic, offset, sampleCount,
                ff);

        return mA;
    }

    static double calculateMomentaryAmplitude(Signal signal, double offset,
        double sampleCount)
    {
        FunctionFactory ff = Functions.getFactory();
        return calculateMomentaryAmplitude(signal, offset, sampleCount, ff);
    }
}
