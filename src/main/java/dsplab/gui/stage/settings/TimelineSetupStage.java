package dsplab.gui.stage.settings;

import dsplab.architecture.stage.StageComm;
import dsplab.gui.prop.TimelineProperties;
import dsplab.gui.ctrl.TimelineSetupController;

public interface TimelineSetupStage extends StageComm<TimelineSetupController>
{
    boolean showModal(TimelineProperties properties);
}
