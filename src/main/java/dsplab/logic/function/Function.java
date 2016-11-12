package dsplab.logic.function;

import dsplab.logic.signal.Harmonic;

public interface Function
{
    double calculate(Harmonic signHarm, double offset, double resolution);
}
