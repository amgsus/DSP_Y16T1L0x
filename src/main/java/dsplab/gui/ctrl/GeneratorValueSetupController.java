package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;
import dsplab.logic.gen.modifier.ValueModifier;

public interface GeneratorValueSetupController extends Controller
{
    void peekValuesFrom(ValueModifier instance);
    ValueModifier buildInstance() throws IllegalArgumentException;































    void setCaption(String s);
    // ...

    boolean isEnabled();
}
