package dsplab.logic.gen.modifier;

public interface ValueModifier
{
    void setSize(int size);

    void setStartValue(double value);
    void setFinalValue(double value);

    int getSize();
    double getStartValue();
    double getFinalValue();

    void setCoefficientBounds(double startValue, double finalValue);

    double[] getAllValues();
    double[] getAllValues(int size);
    double[] getAllValues(int size, double startValue, double finalValue);

    boolean isNegativeDelta();
}
