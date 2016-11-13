package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;

import java.util.function.Supplier;

public interface SignalRestoreController extends Controller
{
    void renderAll();
    void renderSignal();
    void renderRestoredSignal();

    void setSignalSupplier(Supplier<double[]> supplier);
    void setRestoredSignalSupplier(Supplier<double[]> supplier);
    void setRestoredWithPhaseSignalSupplier(Supplier<double[]> supplier);
}
