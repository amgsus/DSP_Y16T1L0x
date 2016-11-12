package dsplab.gui.ctrl;

import dsplab.architecture.callback.more.DialogDelegates;
import dsplab.architecture.ctrl.Controller;
import dsplab.gui.prop.TimelineProperties;

public interface TimelineSetupController extends Controller, DialogDelegates
{
    void setEditableProps(TimelineProperties properties);
}
