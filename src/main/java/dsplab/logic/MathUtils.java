package dsplab.logic;

import java.util.function.Function;

public final class MathUtils
{
    MathUtils() {}

    public static double sum(int from, int countTo,
        Function<Integer, Double> f)
    {
        double s = 0;

        for (int i = from; i < countTo; i++)
            s += f.apply(i);

        return s;
    }
}
