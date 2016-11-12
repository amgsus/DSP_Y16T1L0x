package dsplab.logic.function.impl;

import dsplab.logic.function.Function;
import dsplab.logic.signal.Harmonic;

public class CosineFunction implements Function
{
    CosineFunction() {}

    static Function instance = new CosineFunction();
    public static Function getInstance() { return instance; }

    @Override
    public double calculate(Harmonic signHarm, double offset,
        double resolution)
    {
        double a = signHarm.getAmplitude();
        double p = signHarm.getPhase();
        double f = signHarm.getFrequency();

        p = Math.toRadians(p);

        return a * Math.cos(2 * Math.PI * offset * f / resolution + p);
    }
}
