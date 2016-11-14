package dsplab.logic.function.impl.extended;

import dsplab.logic.MathUtils;
import dsplab.logic.function.Function;
import dsplab.logic.function.CompositeFunction;
import dsplab.logic.signal.Harmonic;

import static dsplab.common.Const.ONE;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

public class TriangleFunction implements CompositeFunction
{
    public TriangleFunction()
    {
        setHarmonicCount(32);
    }

    public static Function newInstance() { return new TriangleFunction(); }

    protected int harmonicCount;

    @Override
    public
    void setHarmonicCount(int value)
    {
        harmonicCount = value;
    }

    @Override
    public double calculate(Harmonic h, double x, double period)
    {
        double a = h.getAmplitude();
        double f = h.getFrequency();
        double _2PiFxT = 2 * Math.PI * f * x / period;

        double sum = MathUtils.sum(ONE, this.harmonicCount, 2, k ->
            pow(-1, (k - 1) / 2) * sin(_2PiFxT * k) / pow(k, 2));

        return 8 * a * sum / PI / PI;
    }
}
