package dsplab.architecture;

import dsplab.architecture.ctrl.Controller;

public interface StageWithController<CtrlType extends Controller>
{
    CtrlType getController();
}
