package dsplab.logic.function.impl.extended;

import dsplab.logic.MathUtils;
import dsplab.logic.function.Function;
import dsplab.logic.signal.Harmonic;

public class SawtoothFunction implements Function
{
    SawtoothFunction() {}

    static Function instance = new SawtoothFunction();
    public static Function getInstance() { return instance; }

    public static final int N = 32;

    @Override
    public double calculate(Harmonic signHarm, double offset,
        double resolution)
    {
        double sum = MathUtils.sum(1, N, k -> Math.pow(-1, k) *
            Math.sin(2 * Math.PI * k * signHarm.getFrequency() * offset /
            resolution + Math.toRadians(signHarm.getPhase())) / k);

        return signHarm.getAmplitude() / 2 -
            signHarm.getAmplitude() * sum / Math.PI;
    }
}
