package dsplab.logic.eq.alg;

public enum FilterType
{
    BAND("Band"), LOWPASS("LF"), HIGHPASS("HF");

    // -------------------------------------------------------------------- //

    private String string;

    FilterType(String toString)
    {
        this.string = toString;
    }

    @Override
    public String toString()
    {
        return string;
    }
}
