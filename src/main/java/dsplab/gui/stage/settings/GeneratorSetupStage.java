package dsplab.gui.stage.settings;

import dsplab.architecture.stage.StageComm;
import dsplab.gui.ctrl.GeneratorSetupController;
import dsplab.gui.prop.GeneratorWithValueModifiersProperties;

public interface GeneratorSetupStage extends StageComm<GeneratorSetupController>
{
    boolean showModal(GeneratorWithValueModifiersProperties properties);
}
