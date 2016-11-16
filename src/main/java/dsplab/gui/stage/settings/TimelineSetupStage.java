package dsplab.gui.stage.settings;

import dsplab.architecture.stage.StageComm;
import dsplab.gui.ctrl.TimelineSetupController;
import dsplab.gui.prop.TimelineProperties;

public interface TimelineSetupStage extends StageComm<TimelineSetupController>
{
    boolean showModal(TimelineProperties properties);
}
