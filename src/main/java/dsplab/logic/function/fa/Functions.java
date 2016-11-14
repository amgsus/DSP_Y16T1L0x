package dsplab.logic.function.fa;

import dsplab.logic.function.Function;
import dsplab.logic.function.impl.CosineFunction;
import dsplab.logic.function.impl.SineFunction;
import dsplab.logic.function.impl.extended.DigitalFunction;
import dsplab.logic.function.impl.extended.SawtoothFunction;
import dsplab.logic.function.impl.extended.NoiseFunction;
import dsplab.logic.function.impl.extended.TriangleFunction;
import dsplab.logic.signal.enums.Waveform;

public final class Functions implements FunctionFactory
{
    Functions() {}
    static final Functions factory = new Functions();

    public static Functions getFactory() { return factory; }

    @Override
    public Function getFunction(Waveform waveform)
    {
        switch (waveform) {
            case Sine:
                return SineFunction.getInstance();
            case Cosine:
                return CosineFunction.getInstance();
            case Noise:
                return NoiseFunction.getInstance();
            case Sawtooth:
                return SawtoothFunction.newInstance();
            case Triangle:
                return TriangleFunction.newInstance();
            case Digital:
                return DigitalFunction.newInstance();

            default:
                final String s = "'%s' is not supported by the factory";
                throw new IllegalArgumentException(String.format(s,
                    waveform.toString()));
        }
    }
}
