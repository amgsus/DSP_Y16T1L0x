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
        if (Double.compare(value, 0) >= 0 && Double.compare(value, 1) <= 0)
            this.value = value;
        else
            throw new IllegalArgumentException("Value is not between 0 and 1");
    }

    @Override
    public
    String toString()
    {
        return String.format("%d %%", (int) (value * 100));
    }
}
