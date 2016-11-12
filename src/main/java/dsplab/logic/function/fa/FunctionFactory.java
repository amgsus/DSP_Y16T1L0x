package dsplab.logic.function.fa;

import dsplab.logic.function.Function;
import dsplab.logic.signal.enums.Waveform;

public interface FunctionFactory
{
    Function getFunction(Waveform waveform);
}
