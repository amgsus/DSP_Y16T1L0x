package dsplab.logic.function.impl.extended;

import dsplab.logic.MathUtils;
import dsplab.logic.function.Function;
import dsplab.logic.signal.Harmonic;

import static dsplab.common.Const.ONE;
import static java.lang.Math.PI;
import static java.lang.Math.sin;

public class DigitalFunction implements Function
{
    public DigitalFunction()
    {
        setMaxK(512);
    }

    public static Function newInstance() { return new DigitalFunction(); }

    protected int sumTo;

    public
    void setMaxK(int value)
    {
        this.sumTo = value;
    }

    @Override
    public double calculate(Harmonic h, double x, double period)
    {
        double a = h.getAmplitude();
        double f = h.getFrequency();
        double _2PiFxT = 2 * Math.PI * f * x / period;

        double sum = MathUtils.sum(ONE, this.sumTo, 2,
            k -> sin(_2PiFxT * k) / k);

        return 4 * a * sum / PI;
    }
}
