package dsplab.logic._;

public class Signal
{
    private double amplitude;
    private double phase;
    private double frequency;

    public Signal()
    {
        this(0.0, 0.0, 0.0);
    }

    public Signal(double amplitude, double phase, double frequency)
    {
        this.amplitude = amplitude;
        this.phase = phase;
        this.frequency = frequency;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getPhase() {
        return phase;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setAmplitude(double amplitude)
    {
        this.amplitude = amplitude;
    }

    public void setPhase(double phase)
    {
        this.phase = phase;
    }

    public void setFrequency(double frequency)
    {
        this.frequency = frequency;
    }
}
