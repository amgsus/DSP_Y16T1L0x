package dsplab.logic._;

public abstract class AbstractFunction
{
    public final double amplitude;
    public final double phase;
    public final double frequency;
    public final int nBolshoe;

    public AbstractFunction(double amplitude, double phase, double frequency,
        int nBolshoe)
    {
        this.amplitude = amplitude;
        this.phase = phase;
        this.frequency = frequency;
        this.nBolshoe = nBolshoe;
    }

    public abstract double calculateY(double posX);
}
