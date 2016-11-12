package dsplab.logic.gen.alg;

public enum GenID
{
    DEFAULT("Default"),
    WITH_VALUE_MODIFIERS("With Value Modifiers");

    // -------------------------------------------------------------------- //

    String textNotation;

    GenID(String stringNotation)
    {
        textNotation = stringNotation;
    }

    @Override
    public String toString()
    {
        return this.textNotation;
    }
}
