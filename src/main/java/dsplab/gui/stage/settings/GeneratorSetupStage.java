package dsplab.gui.stage.settings;

import dsplab.architecture.stage.StageComm;
import dsplab.gui.ctrl.GeneratorSetupController;
import dsplab.logic.gen.modifier.ValueModifier;

public interface GeneratorSetupStage extends
    StageComm<GeneratorSetupController>
{
    boolean showModal(ValueModifier ampModifier, ValueModifier phsModifier,
        ValueModifier frqModifier);
    ValueModifier getNewAmplitudeModifierInstance();
    ValueModifier getNewPhaseModifierInstance();
    ValueModifier getNewFrequencyModifierInstance();
}
