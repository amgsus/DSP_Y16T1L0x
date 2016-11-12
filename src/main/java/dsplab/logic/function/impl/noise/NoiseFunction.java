package dsplab.logic.function.impl.noise;

import dsplab.logic.function.Function;
import dsplab.logic.signal.Harmonic;

import java.util.concurrent.ThreadLocalRandom;

import static dsplab.logic.MathUtils.sum;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

public class NoiseFunction implements Function
{
    NoiseFunction() {}

    static Function instance = new NoiseFunction();
    public static Function getInstance() { return instance; }

    public static final double B2 = 0.0625; // ToDo: Variant!

    @Override
    public double calculate(Harmonic signHarm, double offset,
        double resolution)
    {
        double phase0 = Math.toRadians(signHarm.getPhase());
        double sinArg
            = 2 * Math.PI * offset * signHarm.getFrequency() / resolution;

        double exp = ThreadLocalRandom.current().nextBoolean() ? 1 : 0;

        // 50 & 70 - magic numbers?..
        return signHarm.getAmplitude() * sin(sinArg + phase0) +
            sum(50, 70, j -> pow(-1, exp) * B2 * sin(sinArg * j + phase0));
    }
}
