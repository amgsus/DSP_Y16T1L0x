package dsplab.logic.function.impl.extended;

import dsplab.logic.MathUtils;
import dsplab.logic.function.Function;
import dsplab.logic.function.CompositeFunction;
import dsplab.logic.signal.Harmonic;

import static dsplab.common.Const.ONE;
import static java.lang.Math.sin;

public class SawtoothFunction implements CompositeFunction
{
    public SawtoothFunction()
    {
        setHarmonicCount(32);
        setModifier(2);
    }

    public static Function newInstance() { return new SawtoothFunction(); }

    protected int harmonicCount;
    protected int ampModifier;

    @Override
    public
    void setHarmonicCount(int value)
    {
        harmonicCount = value;
    }

    public
    void setModifier(int value)
    {
        this.ampModifier = value;
    }

    @Override
    public double calculate(Harmonic h, double x, double period)
    {
        double a = h.getAmplitude();
        double f = h.getFrequency();
        double _2PiFxT = 2 * Math.PI * f * x / period;

        double sum = MathUtils.sum(ONE, this.harmonicCount,
            k -> sin(_2PiFxT * k) / k);

        return a / ampModifier - a * sum / Math.PI;
    }
}
