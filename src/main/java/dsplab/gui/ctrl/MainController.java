package dsplab.gui.ctrl;

import dsplab.architecture.ctrl.Controller;

import java.io.File;

public interface MainController extends Controller
{
    void loadSignalList(File file);
    void saveSignalList(File file);
}
