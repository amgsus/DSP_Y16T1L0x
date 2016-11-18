package dsplab.logic.gen.impl;

import dsplab.logic.function.fa.CompositeFunctionFactory;
import dsplab.logic.function.fa.FunctionFactory;
import dsplab.logic.gen.Generator;
import dsplab.logic.gen.GeneratorWithModifiers;
import dsplab.logic.gen.modifier.ValueModifier;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.Signal;
import dsplab.logic.signal.util.SigUtils;

/**
 * Adds modifiers for harmonic amplitude, phase and frequency values.
 */
public class GeneratorWithModifiersImpl extends GeneratorImpl implements
    GeneratorWithModifiers
{
    public GeneratorWithModifiersImpl()
    {
        this.amplitudeModifier = null;
        this.phaseModifier = null;
        this.frequencyModifier = null;
    }

    // -------------------------------------------------------------------- //

    protected ValueModifier amplitudeModifier;
    protected ValueModifier phaseModifier;
    protected ValueModifier frequencyModifier;

    // -------------------------------------------------------------------- //

    /**
     * <b>Note:</b> This method does not call the superclass's method {@link
     * GeneratorImpl#run()}.
     */
    @Override
    public double[] run()
    {
        double[] a = new double[this.sampleCount * this.periodCount];

        FunctionFactory ff = new CompositeFunctionFactory(this.sampleCount);

        Signal srcSignal = this.signal;
        Signal modifiedSignal = SigUtils.cloneSignal(this.signal);

        double[] amplitudeModifierValues = null;
        double[] phaseModifierValues = null;
        double[] frequencyModifierValues = null;

        if (amplitudeModifier != null) {
            amplitudeModifierValues =
                amplitudeModifier.getAllValues(this.sampleCount);
        }

        if (phaseModifier != null) {
            phaseModifierValues =
                phaseModifier.getAllValues(this.sampleCount);
        }

        if (frequencyModifier != null) {
            frequencyModifierValues =
                frequencyModifier.getAllValues(this.sampleCount);
        }

        for (int x = this.offset; x < a.length; x++) {

            /* Modify */

            for (int i = 0; i < modifiedSignal.getHarmonics().size(); i++) {

                Harmonic modifiedHarmonic
                    = modifiedSignal.getHarmonics().get(i);
                Harmonic srcHarmonic = srcSignal.getHarmonics().get(i);

                double newA = srcHarmonic.getAmplitude();

                if (amplitudeModifierValues != null)
                    newA *= amplitudeModifierValues[x];

                double newP = srcHarmonic.getPhase();

                if (phaseModifierValues != null)
                    newP *= phaseModifierValues[x];

                double newF = srcHarmonic.getFrequency();

                if (frequencyModifierValues != null)
                    newF *= frequencyModifierValues[x];

                modifiedHarmonic.setAmplitude(newA);
                modifiedHarmonic.setPhase(newP);
                modifiedHarmonic.setFrequency(newF);
            }

            /* Calculate */

            a[x] = Generator.calculateMomentaryAmplitude(modifiedSignal,
                x, this.sampleCount, ff);
        }

        return a;
    }

    // -------------------------------------------------------------------- //

    @Override
    public
    void setAmplitudeModifier(ValueModifier m)
    {
        this.amplitudeModifier = m;
    }

    @Override
    public
    void setPhaseModifier(ValueModifier m)
    {
        this.phaseModifier = m;
    }

    @Override
    public
    void setFrequnecyModifier(ValueModifier m)
    {
        this.frequencyModifier = m;
    }
}
