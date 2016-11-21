package dsplab.logic.function.impl.extended;

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

    @Override
    public double calculate(Harmonic h, double x, double period)
    {
        double phase0 = Math.toRadians(h.getPhase());
        double sinArg
            = 2 * Math.PI * x * h.getFrequency() / period;

        double exp = ThreadLocalRandom.current().nextBoolean() ? 1 : 0;
        double b2 = h.getAmplitude() / 25;

        // 50 & 70 - magic numbers?..
        return h.getAmplitude() * sin(sinArg + phase0) +
            sum(50, 70, j -> pow(-1, exp) * b2 * sin(sinArg * j + phase0));
    }
}
