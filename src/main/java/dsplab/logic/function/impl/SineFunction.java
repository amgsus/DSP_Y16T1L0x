package dsplab.logic.function.impl;

import dsplab.logic.function.Function;
import dsplab.logic.signal.Harmonic;

public class SineFunction implements Function
{
    SineFunction() {}

    static Function instance = new SineFunction();
    public static Function getInstance() { return instance; }

    @Override
    public double calculate(Harmonic h, double x, double period)
    {
        double a = h.getAmplitude();
        double p = h.getPhase();
        double f = h.getFrequency();

        p = Math.toRadians(p);

        return a * Math.sin(2 * Math.PI * x * f / period + p);
    }
}
