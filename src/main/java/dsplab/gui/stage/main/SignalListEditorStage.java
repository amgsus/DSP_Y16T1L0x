package dsplab.gui.stage.main;

import dsplab.architecture.stage.StageComm;
import dsplab.gui.ctrl.SignalListEditorController;
import dsplab.logic.signal.Signal;

import java.util.List;

public interface SignalListEditorStage extends
    StageComm<SignalListEditorController>
{
    boolean show(List<Signal> signals);
    List<Signal> getModifiedList();
}
