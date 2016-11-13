package dsplab.logic.signal.enums;

public enum Waveform
{
    Sine("Sine"), Cosine("Cosine"), Noise("Sine + Noise");

    // -------------------------------------------------------------------- //

    String text;

    Waveform(String text)
    {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return this.text;
    }
}
