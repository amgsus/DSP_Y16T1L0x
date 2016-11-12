package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;
import dsplab.logic.algo.production.AlgorithmResult;

public interface EachSignalTabController extends Controller
{
    void renderAlgoResult(AlgorithmResult algoResult);
}
