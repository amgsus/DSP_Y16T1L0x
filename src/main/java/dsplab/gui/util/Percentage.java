package dsplab.gui.util;

public class Percentage
{
    public Percentage()
    {
        setValue(1);
    }

    public Percentage(double value)
    {
        setValue(value);
    }

    private double value;

    public
    double getValue()
    {
        return value;
    }

    public
    void setValue(double value)
    {
        if (Double.compare(value, 0) < 0)
            throw new IllegalArgumentException("Percentage must be positive");

        this.value = value;
    }

    @Override
    public
    String toString()
    {
        return String.format("%d %%", (int) (value * 100));
    }
}
