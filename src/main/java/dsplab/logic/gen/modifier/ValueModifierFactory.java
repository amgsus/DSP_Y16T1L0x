package dsplab.logic.gen.modifier;

import dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm;
import dsplab.logic.gen.modifier.impl.concrete.LinearValueModifier;
import dsplab.logic.gen.modifier.impl.concrete.LogarithmicValueModifier;

public final class ValueModifierFactory
{
    private ValueModifierFactory() {}

    private static ValueModifierFactory factory = new ValueModifierFactory();
    public  static ValueModifierFactory getFactory() { return factory; }

    // -------------------------------------------------------------------- //

    @SuppressWarnings("unchecked")
    public <T extends ValueModifier> T newValueModifier
        (ValueModifierAlgorithm algorithm)
    {
        switch (algorithm) {
            case LINEAR:
                return (T) new LinearValueModifier();
            case LOGARITHMIC:
                return (T) new LogarithmicValueModifier();

            default:
                final String msg = "'%s' is not supported by the factory";
                throw new IllegalArgumentException(String.format(msg,
                    algorithm.toString()));
        }
    }
}
