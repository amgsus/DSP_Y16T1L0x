package dsplab.gui.ctrl;

import dsplab.architecture.callback.more.DialogDelegates;
import dsplab.architecture.ctrl.Controller;
import dsplab.logic.signal.Signal;

import java.util.List;

public interface SignalListEditorController extends
    Controller, DialogDelegates
{
    void setEditableList(List<Signal> list);
    List<Signal> getModifiedList();
}
