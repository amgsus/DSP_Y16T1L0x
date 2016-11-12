package dsplab.logic.gen;

import dsplab.logic.gen.modifier.ValueModifier;
import dsplab.logic.signal.Harmonic;

public interface GeneratorWithModifiers extends Generator
{
    /**
     * Sets a modifier for {@link Harmonic#getAmplitude()}.
     * @param m The instance of modifier or {@code null} if no modifier.
     */
    void setAmplitudeModifier(ValueModifier m);

    /**
     * Sets a modifier for {@link Harmonic#getPhase()}.
     * @param m The instance of modifier or {@code null} if no modifier.
     */
    void setPhaseModifier(ValueModifier m);

    /**
     * Sets a modifier for {@link Harmonic#getFrequency()}.
     * @param m The instance of modifier or {@code null} if no modifier.
     */
    void setFrequnecyModifier(ValueModifier m);
}
