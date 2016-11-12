package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;
import dsplab.gui.util.AcceptsBinding;
import dsplab.logic.signal.Harmonic;

public interface HarmonicEditorController extends Controller,
    AcceptsBinding<Harmonic>
{
}
