package dsplab.architecture.util.fa.more;

import dsplab.architecture.ctrl.Controller;
import dsplab.architecture.util.fa.Factory;

public interface ControllerFactory<ControllerType extends Controller> extends
    Factory<ControllerType>
{
    @Override
    ControllerType makeOne();
}
