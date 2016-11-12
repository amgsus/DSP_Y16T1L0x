package dsplab.gui.context.impl;

import dsplab.architecture.ctrl.Controller;
import dsplab.architecture.stage.StageComm;
import dsplab.common.log.Logger;
import dsplab.common.log.fa.LoggerManager;
import dsplab.gui.context.AppContext;
import dsplab.gui.Stages;
import dsplab.gui.stage.main.MainStage;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

import static dsplab.common.Const.MAIN;

public class WTFContext implements AppContext
{
    WTFContext() {}
    public static AppContext newInstance() { return new WTFContext(); }

    Logger logger = LoggerManager.getInstance().getLogger();

    @Override
    public
    void init()
    {
        mainStage = Stages.getFactory().giveMeSomethingLike(MAIN);
    }

    private MainStage mainStage;

    @Override
    public
    void show()
    {
        mainStage.show();
    }

    @Override
    public void releaseStage(Stage stage)
    {
        // ...
    }


    Map<Object, Stage> objectMapping = new WeakHashMap<>();
    Map<Controller, StageComm> ctrlStageMapping = new HashMap<>();

    public
    StageComm<?> getAssociatedStageIfRegistered()
    {
        // ...
        return null;
    }

    @Override
    public ExecutorService getThreadPool()
    {
        return null;
    }
}
