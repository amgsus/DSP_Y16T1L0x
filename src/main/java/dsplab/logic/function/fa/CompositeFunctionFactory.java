package dsplab.logic.function.fa;

import dsplab.logic.function.Function;
import dsplab.logic.function.impl.CosineFunction;
import dsplab.logic.function.impl.SineFunction;
import dsplab.logic.function.impl.extended.DigitalFunction;
import dsplab.logic.function.impl.extended.NoiseFunction;
import dsplab.logic.function.impl.extended.SawtoothFunction;
import dsplab.logic.function.impl.extended.TriangleFunction;
import dsplab.logic.signal.enums.Waveform;

public class CompositeFunctionFactory implements FunctionFactory
{
    public CompositeFunctionFactory()
    {
        this(512); // Magic
    }

    public CompositeFunctionFactory(int compositeHarmonicCount)
    {
        sawtoothFunction.setHarmonicCount(compositeHarmonicCount);
        triangleFunction.setHarmonicCount(compositeHarmonicCount);
        digitalFunction .setHarmonicCount(compositeHarmonicCount);
    }

    public static CompositeFunctionFactory newInstance() { return new
        CompositeFunctionFactory(); }

    // -------------------------------------------------------------------- //

    private SawtoothFunction sawtoothFunction = new SawtoothFunction();
    private TriangleFunction triangleFunction = new TriangleFunction();
    private DigitalFunction digitalFunction = new DigitalFunction();

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
                return sawtoothFunction;
            case Triangle:
                return triangleFunction;
            case Digital:
                return digitalFunction;

            default:
                final String s = "'%s' is not supported by the factory";
                throw new IllegalArgumentException(String.format(s,
                    waveform.toString()));
        }
    }
}
