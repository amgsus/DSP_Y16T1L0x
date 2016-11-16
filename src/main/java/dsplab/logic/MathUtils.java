package dsplab.logic;

import java.util.function.Function;

public final class MathUtils
{
    MathUtils() {}

    public static double sum(int from, int countTo, int step,
        Function<Integer, Double> f)
    {
        double s = 0;

        for (int i = from; i <= countTo; i += step)
            s += f.apply(i);

        return s;
    }

    public static double sum(int from, int countTo,
        Function<Integer, Double> f)
    {
        return sum(from, countTo, 1, f);
    }

    public static double avg(double[] numbers, int from, int count)
    {
        double sum = sum(from, from + count - 1, i -> numbers[i]);
        return sum / count;
    }
}
