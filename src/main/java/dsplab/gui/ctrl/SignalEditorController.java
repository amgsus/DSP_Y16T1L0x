package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;
import dsplab.gui.util.AcceptsBinding;
import dsplab.logic.signal.Signal;

public interface SignalEditorController extends Controller,
    AcceptsBinding<Signal>
{
    void bind(Signal source);
}
