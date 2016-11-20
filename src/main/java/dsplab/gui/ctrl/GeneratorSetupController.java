package dsplab.gui.ctrl;

import dsplab.architecture.callback.more.DialogDelegates;
import dsplab.architecture.ctrl.Controller;
import dsplab.logic.gen.modifier.ValueModifier;

public interface GeneratorSetupController extends Controller, DialogDelegates
{
    ValueModifier getNewAmplitudeModifierInstance();
    ValueModifier getNewPhaseModifierInstance();
    ValueModifier getNewFrequencyModifierInstance();

    void setAmplitudeModifierInstance(ValueModifier modifier);
    void setPhaseModifierInstance(ValueModifier modifier);
    void setFrequencyModifierInstance(ValueModifier modifier);
}
