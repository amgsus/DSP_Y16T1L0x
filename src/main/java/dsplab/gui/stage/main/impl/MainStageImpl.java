package dsplab.gui.stage.main.impl;

import dsplab.architecture.ex.StageInitException;
import dsplab.architecture.stage.StageCommImpl;
import dsplab.common.Const;
import dsplab.gui.Controllers;
import dsplab.gui.ctrl.MainController;
import dsplab.gui.stage.main.MainStage;

import static dsplab.common.Const.MAIN;

public class MainStageImpl extends StageCommImpl<MainController> implements
    MainStage
{
    public MainStageImpl()
    {
        try {
            MainController mainCtrl
                = Controllers.getFactory().giveMeSomethingLike(MAIN);
            init(mainCtrl);
            setTitle(Const.APPTITLE);
        } catch (Throwable cause) {
            throw new StageInitException(ERR_INIT_FAILED, cause);
        }
    }
}
