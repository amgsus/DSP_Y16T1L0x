package dsplab.logic.gen;

public interface GeneratorCallback
{
    /**
     * This callback calls for each value.
     *
     * @param value Calculated value.
     * @param i Index of the value.
     * @param max Max value of {@code i} + 1.
     *
     * @return A value that placed into a result array.
     */
    double apply(double value, int i, int max);
}
