package dsplab.logic.gen.modifier.alg;

public enum ValueModifierAlgorithm
{
    LINEAR("Linear"),
    LOGARITHMIC("Logarithmic");

    // -------------------------------------------------------------------- //

    String textNotation;

    ValueModifierAlgorithm(String textNotation)
    {
        this.textNotation = textNotation;
    }

    @Override
    public String toString()
    {
        return this.textNotation;
    }
}
