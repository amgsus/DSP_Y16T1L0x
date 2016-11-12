package dsplab.logic.signal.util;

import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.Signal;

import java.util.List;

public final class SigUtils
{
    SigUtils() {}

    /**
     * Clones components {@link Signal} object.
     * @param source A source to take values from.
     */
    public
    static Signal cloneSignal(Signal source)
    {
        if (source == null)
            throw new IllegalArgumentException("source is null");

        Signal signal = new Signal(source.getName(), source.getBrushColor());
        for (Harmonic srcHarm : source.getHarmonics())
            signal.getHarmonics().add(newHarmonicLike(srcHarm));

        return signal;
    }

    /**
     * Clones components {@link Harmonic} object.
     * @param source A source to take values from.
     */
    public
    static Harmonic newHarmonicLike(Harmonic source)
    {
        if (source == null)
            throw new IllegalArgumentException("source is null");

        return new Harmonic(source.getAmplitude(), source.getPhase(),
            source.getFrequency(), source.getWaveform());
    }

    /**
     * Clones all elements of the list of {@link Signal}.
     * <p>
     * <b>Note:</b> Only values of {@link Signal} and {@link Harmonic} are
     * cloned, not the properties and bindings.
     * <p>
     * <b>Note:</b> The existing elements of {@code destination} will not be
     * deleted (appending mode).
     *
     * @param source The source list.
     * @param destination The list into which the elements of {@code source}
     * is cloned.
     *
     * @return The number of elements that have been cloned.
     *
     * @throws IllegalArgumentException if one of the method parameters is
     * {@code null}.
     *
     * @see #cloneSignal(Signal)
     * @see #newHarmonicLike(Harmonic)
     */
    public static
    int cloneSignalList(List<Signal> source, List<Signal> destination)
    {
        if (source == null)
            throw new IllegalArgumentException("source");
        if (destination == null)
            throw new IllegalArgumentException("destination");

        source.forEach(signal ->
            destination.add(SigUtils.cloneSignal(signal)));

        return destination.size();
    }
}
