package dsplab.logic.gen.modifier.alg;

public enum Operation
{
    SUM("Sum"), MULTIPLICATION("Multiplication"), DIVISION("Division");

    final String toStrValue;

    Operation(String toStringValue)
    {
        toStrValue = toStringValue;
    }

    @Override
    public String toString()
    {
        return toStrValue;
    }

    public double apply(double numA, double numB)
    {
        switch (this) {
            case SUM: return numA + numB;
            case MULTIPLICATION: return numA * numB;
            case DIVISION: return numA / numB;

            default:
                throw new RuntimeException("This should not ever happen!");
        }
    }
}
