package dsplab.gui.prop;

public class GeneratorWithValueModifiersProperties
{
    private ValueModifierProperties amplitudeModifierProperties =
        new ValueModifierProperties();

    private ValueModifierProperties phaseModifierProperties =
        new ValueModifierProperties();

    private ValueModifierProperties frequencyModifierProperties =
        new ValueModifierProperties();

    public
    ValueModifierProperties getAmplitudeModifierProperties()
    {
        return amplitudeModifierProperties;
    }

    public
    ValueModifierProperties getPhaseModifierProperties()
    {
        return phaseModifierProperties;
    }

    public
    ValueModifierProperties getFrequencyModifierProperties()
    {
        return frequencyModifierProperties;
    }
}
